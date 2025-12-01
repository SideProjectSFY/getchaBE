# 더미데이터 10개 세팅 (테스트 하고 싶을 시 넣기~!)

-- user
INSERT INTO user (id, liked_anime_id1, liked_anime_id2, liked_anime_id3, name, nickname, email, password, is_auth, account_num, account_bank)
VALUES
    (1, 1, 2, 3, '홍길동', '애니마스터', 'user1@test.com', 'pass1', TRUE, '110-023-123456', '신한'),
    (2, 2, 3, 4, '김영희', '덕후여신', 'user2@test.com', 'pass2', TRUE, '133-020-987654', '국민'),
    (3, 3, 4, 5, '박철수', '굿즈헌터', 'user3@test.com', 'pass3', TRUE, '220-333-984321', '농협'),
    (4, 4, 5, 6, '이지훈', '리미티드수집가', 'user4@test.com', 'pass4', TRUE, '009-884-238472', '기업'),
    (5, 5, 6, 7, '최민지', '피규어덕후', 'user5@test.com', 'pass5', TRUE, '330-221-009843', '하나'),
    (6, 6, 7, 8, '정다혜', '원신여신', 'user6@test.com', 'pass6', TRUE, '450-118-003828', '신한'),
    (7, 7, 8, 9, '윤하늘', '굿즈부자', 'user7@test.com', 'pass7', TRUE, '921-344-238712', '국민'),
    (8, 8, 9, 10, '강호동', '애니킹', 'user8@test.com', 'pass8', TRUE, '339-880-238901', '우리'),
    (9, 9, 10, 1, '서주현', '스파이패밀리러버', 'user9@test.com', 'pass9', TRUE, '113-002-998812', '카카오'),
    (10, 10, 1, 2, '이민호', '만화책수집가', 'user10@test.com', 'pass10', TRUE, '440-229-332118', '농협');

-- tmdb_anime
INSERT INTO tmdb_anime (id, title, poster_url, overview, vote_average, vote_count, popularity)
VALUES
    (1, '귀멸의 칼날', 'url1', '귀멸의 칼날 개요', 8.9, 10233, 150.2),
    (2, '원신', 'url2', '원신 개요', 8.3, 8232, 130.5),
    (3, '스파이 패밀리', 'url3', '스파이 패밀리 개요', 9.0, 12833, 170.4),
    (4, '주술회전', 'url4', '주술회전 개요', 8.7, 11233, 155.1),
    (5, '하이큐', 'url5', '하이큐 개요', 8.6, 8821, 120.3),
    (6, '나루토', 'url6', '나루토 개요', 9.1, 22333, 180.9),
    (7, '도쿄 리벤저스', 'url7', '도쿄 리벤저스 개요', 8.5, 7123, 110.4),
    (8, '나의 히어로 아카데미아', 'url8', '나히아 개요', 8.8, 14000, 160.2),
    (9, '원피스', 'url9', '원피스 개요', 9.5, 33000, 210.8),
    (10, '강철의 연금술사', 'url10', '강철 개요', 9.2, 19222, 190.1);

-- tmdb_genre
INSERT INTO tmdb_genre (id, name)
VALUES
    (1, '액션'),
    (2, '코미디'),
    (3, '판타지'),
    (4, '드라마'),
    (5, '모험'),
    (6, '로맨스'),
    (7, '스포츠'),
    (8, '미스터리'),
    (9, '스릴러'),
    (10, 'SF');

-- anime_genre
INSERT INTO anime_genre (tmdb_genre_id, anime_id)
VALUES
    (1,1),
    (2,2),
    (3,3),
    (4,4),
    (5,5),
    (6,6),
    (7,7),
    (8,8),
    (9,9),
    (10,10);

-- goods_image
INSERT INTO goods_image (goods_id, file_path, origin_filename, stored_filename, file_size, sort_order)
VALUES
    (1, 'img/1_1.jpg', '탄지로1.jpg', '1_1.jpg', 1200, 1),
    (2, 'img/2_1.jpg', '파이몬1.jpg', '2_1.jpg', 1300, 1),
    (3, 'img/3_1.jpg', '아냐1.jpg', '3_1.jpg', 1400, 1),
    (4, 'img/4_1.jpg', '이타도리1.jpg', '4_1.jpg', 1100, 1),
    (5, 'img/5_1.jpg', '하이큐1.jpg', '5_1.jpg', 1250, 1),
    (6, 'img/6_1.jpg', '나루토1.jpg', '6_1.jpg', 1500, 1),
    (7, 'img/7_1.jpg', '도리벤1.jpg', '7_1.jpg', 1600, 1),
    (8, 'img/8_1.jpg', '데쿠1.jpg', '8_1.jpg', 1700, 1),
    (9, 'img/9_1.jpg', '원피스1.jpg', '9_1.jpg', 1800, 1),
    (10,'img/10_1.jpg','에드워드1.jpg','10_1.jpg',2000,1);

-- wallet_history
INSERT INTO wallet_history (wallet_id, goods_id, transaction_type, amount, description)
VALUES
    (1, 1, 'CHARGE', 50000, '초기 충전'),
    (2, 2, 'BIDLOCK', 10000, '경매 입찰금 잠금'),
    (3, 3, 'CHARGE', 80000, '충전 완료'),
    (4, 4, 'BIDLOCK', 20000, '입찰 참여'),
    (5, 5, 'EXPENSE', 3000, '굿즈 구매'),
    (6, 6, 'INCOME', 70000, '판매 수익'),
    (7, 7, 'BIDUNLOCK', 15000, '입찰 취소'),
    (8, 8, 'CHARGE', 90000, '충전'),
    (9, 9, 'CHARGE', 100000, '충전'),
    (10, 10, 'BIDLOCK', 35000, '입찰 잠금');

-- goods
INSERT INTO goods (id, seller_id, anime_id, category, title, description, start_price, instant_buy_price, auction_status, duration, auction_end_at)
VALUES
    (1, 1, 1, 'FIGURE', '탄지로 피규어', '귀멸의 칼날 탄지로 피규어', 15000, 30000, 'PROCEEDING', 3, '2025-12-01 12:00:00'),
    (2, 2, 2, 'KEYRING', '파이몬 키링', '원신 파이몬 키링', 8000, 20000, 'PROCEEDING', 5, '2025-12-03 15:00:00'),
    (3, 3, 3, 'POSTER', '아냐 포스터', '스파이 패밀리 아냐 일러스트 포스터', 5000, 12000, 'WAIT', 7, NULL),
    (4, 4, 4, 'ACRYLICSTAND', '이타도리 아크릴', '주술회전 이타도리 아크릴 스탠드', 7000, 15000, 'PROCEEDING', 2, '2025-11-29 10:00:00'),
    (5, 5, 5, 'PHOTOCARD', '하이큐 포토카드', '하이큐 포토카드 세트', 3000, 10000, 'COMPLETED', 3, '2025-11-25 16:00:00'),
    (6, 6, 6, 'FIGURE', '나루토 피규어', '나루토 전투 피규어', 20000, 40000, 'PROCEEDING', 5, '2025-12-05 19:00:00'),
    (7, 7, 7, 'BADGE', '도리벤 뱃지', '도쿄 리벤저스 뱃지 세트', 4000, 10000, 'STOPPED', 4, NULL),
    (8, 8, 8, 'DOLL', '데쿠 인형', '나히아 데쿠 봉제인형', 12000, 25000, 'WAIT', 6, NULL),
    (9, 9, 9, 'OTHER', '원피스 굿즈', '원피스 컬렉션 굿즈', 9000, 18000, 'PROCEEDING', 3, '2025-12-02 13:00:00'),
    (10, 10, 10, 'FIGURE', '에드워드 엘릭 피규어', '강철의 연금술사 에드 피규어', 25000, 45000, 'PROCEEDING', 8, '2025-12-07 09:00:00');

-- wishlist
INSERT INTO wishlist (goods_id, user_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 3),
    (3, 4),
    (4, 5),
    (5, 6),
    (6, 7),
    (7, 8),
    (8, 9),
    (9, 10);

-- bid
INSERT INTO bid (goods_id, bidder_id, bid_amount, is_highest)
VALUES
    (1, 2, 18000, TRUE),
    (2, 1, 9000, FALSE),
    (2, 3, 12000, TRUE),
    (4, 5, 10000, TRUE),
    (6, 7, 25000, TRUE),
    (9, 8, 12000, FALSE),
    (9, 9, 15000, TRUE),
    (10, 10, 30000, FALSE),
    (10, 4, 35000, TRUE),
    (5, 6, 8000, TRUE);

-- comment
INSERT INTO comment (goods_id, writer_id, parent_id, content)
VALUES
    (1, 1, NULL, '탄지로 피규어 퀄리티 좋아보여요!'),
    (1, 2, 1, '저도 구매했는데 만족했어요'),
    (2, 3, NULL, '파이몬 키링 너무 귀엽다'),
    (3, 4, NULL, '아냐 포스터 사고싶다'),
    (4, 5, NULL, '이타도리 스탠드 퀄 대박'),
    (5, 6, NULL, '포토카드 상태 좋나요?'),
    (6, 7, NULL, '나루토 피규어 장난없네'),
    (7, 8, NULL, '도리벤 굿즈 찾고있었어요'),
    (8, 9, NULL, '데쿠 인형 탐난다'),
    (9, 10, NULL, '원피스 굿즈 구성 좋아요');

-- coin_wallet
INSERT INTO coin_wallet (user_id, balance, locked_balance)
VALUES
    (1, 50000, 0),
    (2, 30000, 10000),
    (3, 80000, 0),
    (4, 60000, 20000),
    (5, 45000, 0),
    (6, 70000, 0),
    (7, 55000, 15000),
    (8, 90000, 0),
    (9, 100000, 30000),
    (10, 65000, 0);
