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

package com.nhnacademy.frontserver.order;

import com.nhnacademy.frontserver.order.util.OrderItemStatus;

public record OrderItemResponse(
        Long orderItemId,
        Long orderId,
        Long bookId,
        String bookName,
        String bookImage,
        int quantity,
        int unitPrice, // 단가
        int totalItemOriginalPrice, // 총 원래 상품 가격 ((단가 + 포장비) * 수량)
        int itemDiscountAmount, // 이 상품 라인에 적용된 할인액
        int totalItemSalePrice, // 이 상품 라인의 최종 결제 금액 (totalItemOriginalPrice - 아이템 할인액)
        int packagingPrice,
        OrderItemStatus orderItemStatus
) {
}