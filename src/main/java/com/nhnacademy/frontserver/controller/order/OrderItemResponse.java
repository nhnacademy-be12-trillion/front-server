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

package com.nhnacademy.frontserver.controller.order;

import com.nhnacademy.frontserver.controller.order.util.OrderItemStatus;

public record OrderItemResponse(
        Long orderId,
        Long orderItemId,
        Long bookId,
        String bookName,
        int quantity,
        int price,
        int packagingPrice,

        // TODO: 도서 이미지 추가 필요
        // String thumbnailUrl,
        OrderItemStatus orderItemStatus
) {
}
