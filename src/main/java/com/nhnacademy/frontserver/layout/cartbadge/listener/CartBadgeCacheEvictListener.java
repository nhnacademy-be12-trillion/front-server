package com.nhnacademy.frontserver.layout.cartbadge.listener;

import com.nhnacademy.frontserver.layout.cartbadge.repository.CartBadgeLocalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
//TODO. 경합문제를 해결하는 방법
// 서버A: 장바구니 수정... 로컬 캐시 갱신 메시지 발행!
// 서버B: 로컬 캐시 갱신 메시지를 수신해서 처리하기 전에, 즉시 장바구니 뱃지 갯수를 보여줄 경우...
// 수정되지 않은 값이 출력 될 수 있음... 자바스크립트 단에서 처리 필요.
public class CartBadgeCacheEvictListener implements MessageListener {

    private final CartBadgeLocalRepository l1Cache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 메시지 내용(body)에는 변경된 cacheKey가 들어옴
        String cacheKey = new String(message.getBody(), StandardCharsets.UTF_8);

        // 내 메모리(L1)에서 해당 키 삭제 -> 다음 조회 시 L2(Redis)에서 가져오게 됨
        l1Cache.evict(cacheKey);

        log.debug("Global Cache Eviction triggered for key: {}", cacheKey);
    }
}