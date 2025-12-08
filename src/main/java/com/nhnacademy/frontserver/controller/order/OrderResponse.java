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

import com.nhnacademy.frontserver.controller.order.util.OrderStatus;
import com.nhnacademy.frontserver.controller.order.util.OrdererInfo;
import com.nhnacademy.frontserver.controller.order.util.ReceiverInfo;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long memberId,
        String orderNumber,
        LocalDateTime orderDate,
        OrderStatus orderStatus,
        int originPrice,
        int totalPrice,
        int deliveryFee,
        OrdererInfo ordererInfo,
        ReceiverInfo receiverInfo,
        List<OrderItemResponse> orderItems
) {
}
