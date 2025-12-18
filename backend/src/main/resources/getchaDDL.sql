CREATE DATABASE IF NOT EXISTS getcha;

use getcha;

create table getcha.goods_image
(
    id              bigint auto_increment primary key,
    goods_id        bigint                              not null,
    file_path       varchar(512)                        not null comment '저장 경로 (서버 디스크 또는 AWS S3)',
    origin_filename varchar(255)                        not null comment '사용자가 업로드한 원본 파일명',
    stored_filename varchar(255)                        not null comment '서버에 저장된 파일명(중복방지를 위해 고유값 사용)',
    file_size       bigint                              null comment '파일명 옆에 크기 보여주기 위함',
    sort_order      int                                 null comment '정렬기준 1번으로 대표이미지 자동설정',
    created_at      timestamp default CURRENT_TIMESTAMP not null
);


create table goods
(
    id                bigint auto_increment primary key,
    seller_id         bigint                                                                                      not null comment '유저ID',
    anime_id          bigint                                                                                      not null comment 'TMDB 애니 ID',
    category          enum ('FIGURE', 'PHOTOCARD', 'ACRYLICSTAND', 'KEYRING', 'DOLL', 'POSTER', 'BADGE', 'OTHER') not null,
    title             varchar(300)                                                                                not null,
    description       text                                                                                        not null,
    start_price       int                                                                                         not null,
    instant_buy_price int                                                                                         null,
    auction_status    enum ('WAIT', 'PROCEEDING', 'COMPLETED', 'STOPPED')                                         not null comment 'DDL 레벨에서 최소한 ENUM',
    duration          int       default 3                                                                         not null comment '2일 ~ 14일',
    auction_end_at    timestamp                                                                                   null comment '명확하게 경매 종료 용도로 정의',
    created_at        timestamp default CURRENT_TIMESTAMP                                                         not null comment '시스템컬럼 개념',
    updated_at        timestamp                                                                                   null comment '시스템컬럼 개념',
    deleted_at        timestamp                                                                                   null comment 'soft deleted'
);


create table wishlist
(
    id         bigint auto_increment primary key,
    goods_id   bigint                              not null,
    user_id    bigint                              not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    constraint uq_wishlist
        unique (goods_id, user_id)
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

create table bid
(
    id         bigint auto_increment primary key,
    goods_id   bigint                               not null,
    bidder_id  bigint                               not null comment '유저 ID',
    bid_amount int                                  not null,
    is_highest tinyint(1) default 0                 not null comment 'boolean 과 같은 타입',
    created_at timestamp  default CURRENT_TIMESTAMP not null
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

create table comment
(
    id         bigint auto_increment primary key,
    goods_id   bigint                              not null,
    writer_id  bigint                              not null comment '유저ID',
    parent_id  bigint                              null comment '댓글ID 를 외래키로 가짐(셀프조인)',
    content    text                                null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp                           null,
    deleted_at timestamp                           null comment 'soft 삭제'
);

create table coin_wallet
(
    id             bigint auto_increment primary key,
    user_id        bigint not null,
    balance        int    not null comment '예치금이 뺀 잔액',
    locked_balance int    null comment '예치금 총액(다수경매)',
    constraint UQ_coin_wallet_user unique (user_id)
);



create table getcha.wallet_history
(
    id               bigint auto_increment primary key,
    wallet_id        bigint                                                       not null,
    goods_id         bigint                                                       null,
    transaction_type enum ('CHARGE', 'BIDLOCK', 'BIDUNLOCK', 'INCOME', 'EXPENSE') not null comment 'DDL 에서 ENUM 제약 걸기',
    amount           int                                                          not null,
    description      text                                                         null,
    created_at       timestamp default CURRENT_TIMESTAMP                          not null
);



CREATE TABLE `anime_genre` (
                               `tmdb_genre_id`	INT	NOT NULL	COMMENT 'TMDB 애니장르',
                               `anime_id`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID',
                               CONSTRAINT `PK_ANIME_GENRE` PRIMARY KEY (`tmdb_genre_id`, `anime_id`)
);

CREATE DATABASE IF NOT EXISTS getcha;

use getcha;

create table getcha.goods_image
(
    id              bigint auto_increment primary key,
    goods_id        bigint                              not null,
    file_path       varchar(512)                        not null comment '저장 경로 (서버 디스크 또는 AWS S3)',
    origin_filename varchar(255)                        not null comment '사용자가 업로드한 원본 파일명',
    stored_filename varchar(255)                        not null comment '서버에 저장된 파일명(중복방지를 위해 고유값 사용)',
    file_size       bigint                              null comment '파일명 옆에 크기 보여주기 위함',
    sort_order      int                                 null comment '정렬기준 1번으로 대표이미지 자동설정',
    created_at      timestamp default CURRENT_TIMESTAMP not null
);


create table goods
(
    id                bigint auto_increment primary key,
    seller_id         bigint                                                                                      not null comment '유저ID',
    anime_id          bigint                                                                                      not null comment 'TMDB 애니 ID',
    category          enum ('FIGURE', 'PHOTOCARD', 'ACRYLICSTAND', 'KEYRING', 'DOLL', 'POSTER', 'BADGE', 'OTHER') not null,
    title             varchar(300)                                                                                not null,
    description       text                                                                                        not null,
    start_price       int                                                                                         not null,
    instant_buy_price int                                                                                         null,
    auction_status    enum ('WAIT', 'PROCEEDING', 'COMPLETED', 'STOPPED')                                         not null comment 'DDL 레벨에서 최소한 ENUM',
    duration          int       default 3                                                                         not null comment '2일 ~ 14일',
    auction_end_at    timestamp                                                                                   null comment '명확하게 경매 종료 용도로 정의',
    created_at        timestamp default CURRENT_TIMESTAMP                                                         not null comment '시스템컬럼 개념',
    updated_at        timestamp                                                                                   null comment '시스템컬럼 개념',
    deleted_at        timestamp                                                                                   null comment 'soft deleted'
);


create table wishlist
(
    id         bigint auto_increment primary key,
    goods_id   bigint                              not null,
    user_id    bigint                              not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    constraint uq_wishlist
        unique (goods_id, user_id)
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

create table bid
(
    id         bigint auto_increment primary key,
    goods_id   bigint                               not null,
    bidder_id  bigint                               not null comment '유저 ID',
    bid_amount int                                  not null,
    is_highest tinyint(1) default 0                 not null comment 'boolean 과 같은 타입',
    created_at timestamp  default CURRENT_TIMESTAMP not null
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

create table comment
(
    id         bigint auto_increment primary key,
    goods_id   bigint                              not null,
    writer_id  bigint                              not null comment '유저ID',
    parent_id  bigint                              null comment '댓글ID 를 외래키로 가짐(셀프조인)',
    content    text                                null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp                           null,
    deleted_at timestamp                           null comment 'soft 삭제'
);

create table coin_wallet
(
    id             bigint auto_increment primary key,
    user_id        bigint not null,
    balance        int    not null comment '예치금이 뺀 잔액',
    locked_balance int    null comment '예치금 총액(다수경매)',
    constraint UQ_coin_wallet_user unique (user_id)
);



create table getcha.wallet_history
(
    id               bigint auto_increment primary key,
    wallet_id        bigint                                                       not null,
    goods_id         bigint                                                       null,
    transaction_type enum ('CHARGE', 'BIDLOCK', 'BIDUNLOCK', 'INCOME', 'EXPENSE') not null comment 'DDL 에서 ENUM 제약 걸기',
    amount           int                                                          not null,
    description      text                                                         null,
    created_at       timestamp default CURRENT_TIMESTAMP                          not null
);



CREATE TABLE `anime_genre` (
                               `tmdb_genre_id`	INT	NOT NULL	COMMENT 'TMDB 애니장르',
                               `anime_id`	BIGINT	NOT NULL	COMMENT 'TMDB 애니 ID',
                               CONSTRAINT `PK_ANIME_GENRE` PRIMARY KEY (`tmdb_genre_id`, `anime_id`)
);

CREATE TABLE payment (
                         id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id       BIGINT NOT NULL,
                         merchant_uid  VARCHAR(64) NOT NULL,     -- 우리 주문번호(중복 방지 핵심)
                         imp_uid       VARCHAR(64) NULL,         -- 포트원 결제 고유번호 (완료 시 저장)
                         amount        INT NOT NULL,             -- 충전 금액(=골드)
                         status        ENUM('READY','PAID','FAILED') NOT NULL DEFAULT 'READY',
                         paid_at       TIMESTAMP NULL,
                         created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at    TIMESTAMP NULL,
                         CONSTRAINT uq_payment_merchant UNIQUE (merchant_uid)
);

