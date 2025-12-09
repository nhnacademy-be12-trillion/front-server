/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.frontserver.controller.order.util;

import java.time.LocalDateTime;

public record OrderDetails(
    LocalDateTime orderDate,
    String shippingPostCode, // 우편번호
    LocalDateTime deliveryDate,
    int deliveryFee,
    int pointUsage,

    // 총 상품 금액: (모든 도서 가격 * 수량) + 모든 도서의 포장비 합
    int originPrice,

    // 최종 청구 금액: originPrice - (총 쿠폰 할인액 + 사용 포인트) + 배송비
    int totalPrice,
    Long couponId
) {
}
