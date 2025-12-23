(function ($) {
    "use strict";

    // Back to top button
    $(window).scroll(function () {
        if ($(this).scrollTop() > 100) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }
    });
    $('.back-to-top').click(function () {
        $('html, body').animate({scrollTop: 0}, 1500, 'easeInOutExpo');
        return false;
    });


    // Vendor carousel (기존 유지)
    $('.vendor-carousel').owlCarousel({
        loop: true,
        margin: 29,
        nav: false,
        autoplay: true,
        smartSpeed: 1000,
        responsive: {
            0:{ items:2 },
            576:{ items:3 },
            768:{ items:4 },
            992:{ items:5 },
            1200:{ items:6 }
        }
    });


    // [수정됨] Related carousel (인기 도서 섹션 - 넷플릭스 스타일)
    $('.related-carousel').owlCarousel({
        loop: false,        // [변경] 데이터가 적을 때 반복 시 오류 방지 & 끝이 있는 리스트 느낌
        margin: 29,
        nav: true,          // 화살표 켜기
        navText: [          // 화살표 아이콘 (FontAwesome)
            '<i class="fa fa-angle-left" aria-hidden="true"></i>',
            '<i class="fa fa-angle-right" aria-hidden="true"></i>'
        ],
        autoplay: false,    // [변경] 넷플릭스처럼 사용자가 클릭할 때만 이동 (자동재생 끔)
        smartSpeed: 1000,
        slideBy: 1,         // 한 번에 하나씩 부드럽게 이동
        dots: false,        // [추가] 하단 점(Indicator) 제거
        responsive: {
            0:{ items:1 },
            576:{ items:2 },
            768:{ items:3 },
            992:{ items:4 },
            1200:{ items:5 } // PC 큰 화면에서 5개 보이기
        }
    });


    // Testimonials carousel (기존 유지)
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        center: true,
        margin: 24,
        dots: true,
        loop: true,
        nav : false,
        responsive: {
            0:{ items:1 },
            576:{ items:2 },
            768:{ items:3 },
            992:{ items:4 }
        }
    });


    // Product Quantity
    $('.quantity button').on('click', function () {
        var button = $(this);
        var oldValue = button.parent().parent().find('input').val();
        if (button.hasClass('btn-plus')) {
            var newVal = parseFloat(oldValue) + 1;
        } else {
            if (oldValue > 0) {
                var newVal = parseFloat(oldValue) - 1;
            } else {
                newVal = 0;
            }
        }
        button.parent().parent().find('input').val(newVal);
    });

    // 카테고리 메뉴 호버 기능 (PC 화면에서만 동작)
    function toggleNavbarMethod() {
        if ($(window).width() > 992) {
            $('.category-hover-area').on('mouseenter', function () {
                $('#navbar-vertical').addClass('show');
            }).on('mouseleave', function () {
                $('#navbar-vertical').removeClass('show');
            });
        } else {
            $('.category-hover-area').off('mouseenter mouseleave');
        }
    }

    toggleNavbarMethod();
    $(window).resize(toggleNavbarMethod);

})(jQuery);