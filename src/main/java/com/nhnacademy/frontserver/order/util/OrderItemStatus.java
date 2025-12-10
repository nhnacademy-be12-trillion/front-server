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

package com.nhnacademy.frontserver.order.util;

public enum OrderItemStatus {
    PREPARING,  // 상품 준비중
    SHIPPED,    // 배송중
    DELIVERED,  // 배송 완료
    RETURNED,   // 반품 완료
    CONFIRMED,  // 구매 확정
    CANCELED,   // 주문 취소

    RETURN_REQUESTED_CHANGE_OF_MIND,    // 반품 요청 (단순 변심)
    RETURN_REQUESTED_DAMAGED,           // 반품 요청 (파손)
}
