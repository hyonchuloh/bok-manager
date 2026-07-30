package com.ohc.bok.mngr.svc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ohc.bok.mngr.dao.BokManagerPostDao;
import com.ohc.bok.mngr.dao.dto.BokManagerPostDto;

@Service
public class BokManagerPostSvcImpl implements BokManagerPostSvc {

    private final BokManagerPostDao dao;

    BokManagerPostSvcImpl(BokManagerPostDao dao) {
        this.dao = dao;
    }

    @Override
    public void initTable() {
        dao.initTable();
    }

    @Override
    public int createPost(String userId, String rawPassword, String contents) {
        BokManagerPostDto entity = new BokManagerPostDto();
        entity.setParentSeq(null);
        entity.setUserId(userId);
        entity.setPassword(hash(rawPassword));
        entity.setContents(contents);
        return dao.insertItem(entity);
    }

    @Override
    public int createReply(String userId, String rawPassword, int parentSeq, String contents) {
        BokManagerPostDto entity = new BokManagerPostDto();
        entity.setParentSeq(parentSeq);
        entity.setUserId(userId);
        entity.setPassword(hash(rawPassword));
        entity.setContents(contents);
        return dao.insertItem(entity);
    }

    @Override
    public List<BokManagerPostDto> getFeed() {
        return dao.getFeedItems();
    }

    @Override
    public BokManagerPostDto getItem(int seq) {
        return dao.selectItem(seq);
    }

    @Override
    public List<BokManagerPostDto> getThread(int rootSeq) {
        List<BokManagerPostDto> flatItems = dao.getThreadItems(rootSeq);

        /* PARENT_SEQ 기준으로 자식 목록을 묶는다(작성순 유지) */
        Map<Integer, List<BokManagerPostDto>> childrenBySeq = new HashMap<>();
        BokManagerPostDto root = null;
        for (BokManagerPostDto item : flatItems) {
            if (item.getParentSeq() == null) {
                root = item;
            } else {
                childrenBySeq.computeIfAbsent(item.getParentSeq(), k -> new ArrayList<>()).add(item);
            }
        }

        List<BokManagerPostDto> ordered = new ArrayList<>();
        if (root != null) {
            appendThread(root, 0, childrenBySeq, ordered);
        }
        return ordered;
    }

    /* 루트에서 시작해 대댓글을 깊이 우선(DFS)으로 순회하며 depth를 부여한다 */
    private void appendThread(BokManagerPostDto node, int depth, Map<Integer, List<BokManagerPostDto>> childrenBySeq, List<BokManagerPostDto> ordered) {
        node.setDepth(depth);
        ordered.add(node);
        List<BokManagerPostDto> children = childrenBySeq.get(node.getSeq());
        if (children == null) {
            return;
        }
        for (BokManagerPostDto child : children) {
            appendThread(child, depth + 1, childrenBySeq, ordered);
        }
    }

    @Override
    public int updateItem(int seq, String contents, String requestUserId, boolean isAdminSession, String suppliedRawPassword) {
        BokManagerPostDto item = dao.selectItem(seq);
        if (item == null) {
            return 0;
        }
        if (!isAuthorized(item, requestUserId, isAdminSession, suppliedRawPassword)) {
            return -1;
        }
        BokManagerPostDto update = new BokManagerPostDto();
        update.setSeq(seq);
        update.setContents(contents);
        return dao.updateItem(update);
    }

    @Override
    public int deleteItem(int seq, String requestUserId, boolean isAdminSession, String suppliedRawPassword) {
        BokManagerPostDto item = dao.selectItem(seq);
        if (item == null) {
            return 0;
        }
        if (!isAuthorized(item, requestUserId, isAdminSession, suppliedRawPassword)) {
            return -1;
        }
        return dao.deleteThreadItems(seq);
    }

    @Override
    public int likeItem(int seq) {
        return dao.likeItem(seq);
    }

    /**
     * 게시물에 대한 수정/삭제 권한을 판별한다.
     * - 관리자 세션이면 항상 허용.
     * - 로그인 세션으로 작성된 게시물(password==null)은 작성자 본인 세션일 때만 허용.
     * - 비로그인(익명)으로 작성된 게시물은 작성 시 등록한 비밀번호가 일치할 때만 허용.
     */
    private boolean isAuthorized(BokManagerPostDto item, String requestUserId, boolean isAdminSession, String suppliedRawPassword) {
        if (isAdminSession) {
            return true;
        }
        if (item.getPassword() == null) {
            return requestUserId != null && requestUserId.equals(item.getUserId());
        }
        return suppliedRawPassword != null && item.getPassword().equals(hash(suppliedRawPassword));
    }

    private static String hash(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
