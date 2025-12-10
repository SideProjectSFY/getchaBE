CREATE DATABASE IF NOT EXISTS getcha;

use getcha;

CREATE TABLE `goods_image` (
                               `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                               `goods_id`	BIGINT	NOT NULL,
                               `file_path`	VARCHAR(512)	NOT NULL	COMMENT '저장 경로 (서버 디스크 또는 AWS S3)',
                               `origin_filename`	VARCHAR(255)	NOT NULL	COMMENT '사용자가 업로드한 원본 파일명',
                               `stored_filename`	VARCHAR(255)	NOT NULL	COMMENT '서버에 저장된 파일명(중복방지를 위해 고유값 사용)',
                               `file_size`	BIGINT	NULL	COMMENT '파일명 옆에 크기 보여주기 위함',
                               `sort_order`	INT	NULL	COMMENT '정렬기준 1번으로 대표이미지 자동설정',
                               `created_at`	TIMESTAMP NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT `PK_GOODS_IMAGE` PRIMARY KEY (`id`)
);

CREATE TABLE `goods` (
                         `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                         `seller_id`	BIGINT NOT NULL	COMMENT '유저ID',
                         `anime_id`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID',
                         `category` ENUM('FIGURE', 'PHOTOCARD', 'ACRYLICSTAND', 'KEYRING', 'DOLL', 'POSTER', 'BADGE', 'OTHER')	NOT NULL,
                         `title`	VARCHAR(300)	NOT NULL,
                         `description`	TEXT	NOT NULL,
                         `start_price`	INT	NOT NULL,
                         `instant_buy_price`	INT	NULL,
                         `auction_status` ENUM('WAIT', 'PROCEEDING', 'COMPLETED', 'STOPPED')	NOT NULL COMMENT 'DDL 레벨에서 최소한 ENUM',
                         `duration`	INT	NOT NULL	DEFAULT 3	COMMENT '2일 ~ 14일',
                         `auction_end_at`	TIMESTAMP	NULL	COMMENT '명확하게 경매 종료 용도로 정의',
                         `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP	COMMENT '시스템컬럼 개념',
                         `updated_at`	TIMESTAMP	NULL	COMMENT '시스템컬럼 개념',
                         `deleted_at`	TIMESTAMP	NULL	COMMENT 'soft deleted',
                         CONSTRAINT `PK_GOODS` PRIMARY KEY (`id`)
);

CREATE TABLE `wishlist` (
                            `id`	BIGINT	NOT NULL  AUTO_INCREMENT,
                            `goods_id`	BIGINT	NOT NULL,
                            `user_id`	BIGINT	NOT NULL,
                            `created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT `PK_WHISHLIST` PRIMARY KEY (`id`)
);

CREATE TABLE `tmdb_genre` (
                              `id`	INT	NOT NULL AUTO_INCREMENT	COMMENT 'TMDB 애니장르',
                              `name`	VARCHAR(50)	NOT NULL,
                              CONSTRAINT `PK_TMDB_GENRE` PRIMARY KEY (`id`)
);

CREATE TABLE `tmdb_anime` (
                              `id`	BIGINT	NOT NULL AUTO_INCREMENT	COMMENT 'TMDB 애니 ID',
                              `title`	VARCHAR(255)	NULL	COMMENT 'TMDB 애니제목',
                              `poster_url`	VARCHAR(255)	NULL	COMMENT 'TMDB 포스터URL',
                              `overview`	TEXT	NULL	COMMENT 'TMDB 애니개요',
                              `vote_average`	DOUBLE	NULL	COMMENT 'TMDB 평점',
                              `vote_count`	BIGINT	NULL	COMMENT 'TMDB 조회수',
                              `popularity`	DOUBLE	NULL	COMMENT 'TMDB 인기지수',
                              `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at`	TIMESTAMP	NULL,
                              CONSTRAINT `PK_TMDB_ANIME` PRIMARY KEY (`id`)
);

CREATE TABLE `notification` (
                                `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                                `user_id`	BIGINT	NOT NULL,
                                `type`	VARCHAR(50)	NOT NULL,
                                `message`	VARCHAR(300)	NOT NULL,
                                `read_at`	TIMESTAMP	NULL,
                                `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `link`	VARCHAR(300)	NULL,
                                CONSTRAINT `PK_NOTIFICATION` PRIMARY KEY (`id`)
);

CREATE TABLE `bid` (
                       `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                       `goods_id`	BIGINT	NOT NULL,
                       `bidder_id`	BIGINT NOT NULL	COMMENT '유저 ID',
                       `bid_amount`	INT	NOT NULL,
                       `is_highest`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                       `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT `PK_BID` PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
                        `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                        `liked_anime_id1`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID1',
                        `liked_anime_id2`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID2',
                        `liked_anime_id3`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID3',
                        `name`	VARCHAR(30)	NULL,
                        `nickname`	VARCHAR(50)	NULL,
                        `email`	VARCHAR(30)	NULL,
                        `password`	VARCHAR(300)	NULL,
                        `is_auth`	BOOLEAN	NULL,
                        `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at`	TIMESTAMP	NULL,
                        `deleted_at`	TIMESTAMP	NULL,
                        `account_num`	VARCHAR(20)	NULL	COMMENT '그냥',
                        `account_bank`	VARCHAR(30)	NULL	COMMENT '그냥',
                        CONSTRAINT `PK_USER` PRIMARY KEY (`id`)
);

CREATE TABLE `comment` (
                           `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                           `goods_id`	BIGINT	NOT NULL,
                           `writer_id`	BIGINT NOT NULL	COMMENT '유저ID',
                           `parent_id`	BIGINT	NULL	COMMENT '댓글ID 를 외래키로 가짐(셀프조인)',
                           `content`	TEXT	NULL,
                           `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `updated_at`	TIMESTAMP	NULL,
                           `deleted_at`	TIMESTAMP	NULL comment 'soft 삭제',
                           CONSTRAINT `PK_COMMENT` PRIMARY KEY (`id`)
);

CREATE TABLE `coin_wallet` (
                               `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                               `user_id`	BIGINT	NOT NULL,
                               `balance`	INT	NOT NULL	COMMENT '예치금이 뺀 잔액',
                               `locked_balance`	INT	NULL	COMMENT '예치금 총액(다수경매)',
                               CONSTRAINT `PK_COIN_WALLET` PRIMARY KEY (`id`),
                               CONSTRAINT UQ_COIN_WALLET_USER UNIQUE (user_id)
);

CREATE TABLE `wallet_history` (
                                  `id`	BIGINT	NOT NULL AUTO_INCREMENT,
                                  `wallet_id`	BIGINT	NOT NULL,
                                  `goods_id`	BIGINT	NULL,
                                  `transaction_type`	ENUM('CHARGE', 'BIDLOCK', 'BIDUNLOCK', 'INCOME', 'EXPENSE')	NOT NULL	COMMENT 'DDL 에서 ENUM 제약 걸기',
                                  `amount`	INT	NOT NULL,
                                  `description` TEXT	NULL,
                                  `created_at`	TIMESTAMP	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT `PK_WALLET_HISTORY` PRIMARY KEY (`id`)
);

CREATE TABLE `anime_genre` (
                               `tmdb_genre_id`	INT	NOT NULL	COMMENT 'TMDB 애니장르',
                               `anime_id`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID',
                               CONSTRAINT `PK_ANIME_GENRE` PRIMARY KEY (`tmdb_genre_id`, `anime_id`)
);

