/*
Instruction:
1. Create user and db
create role stock with login connection limit 100 password '0228';
create database stock /*with owner = stock;
2. Modify pg_hba.conf and postgresql.conf to allow access and restart
*/

/*
--Schema related DDL and DML
*/
--begin;
create table exchange (
	id integer,
	description text not null,
	constraint exchange_pkey primary key (id)
);

insert into exchange values
	(1, 'NYSE'),
	(2, 'NASDAQ');

create sequence seq_company_id;
create table company (
	id bigint default nextval('seq_company_id'),
    name text,
    ticker text not null,
    exchange_id integer not null,
    is_sp500 boolean not null,
    is_russell3000 boolean not null,
    create_date timestamp with time zone,
    update_date timestamp with time zone,
    is_active boolean default true,
    constraint company_pkey primary key (id),
    constraint exchange_id_fkey foreign key (exchange_id) references exchange (id)
);
alter sequence seq_company_id owned by company.id;
create unique index company_ticker_idx on company (ticker);

create sequence seq_stock_price_id;
create table stock_price (
	id bigint default nextval('seq_stock_price_id'),
    company_id bigint,
    date bigint not null,
    close numeric,
    high numeric,
    low numeric,
    open numeric,
    volume bigint,
    create_date timestamp with time zone,
    constraint stock_price_pkey primary key(id),
    constraint stock_price_company_id_fkey foreign key (company_id) references company (id)
);
alter sequence seq_stock_price_id owned by stock_price.id;
create unique index stock_price_company_id_date_idx on stock_price (company_id, date);

create table workflow_status_code (
	id integer,
    description text,
    constraint workflow_status_code_pkey primary key (id)
);

insert into workflow_status_code values
	(1, 'READY'),
    (2, 'IN PROGRESS'),
    (3, 'DONE'),
    (4, 'ERROR');

create sequence seq_workflow_status_company_id;
create table workflow_status_company (
	id bigint default nextval('seq_workflow_status_company_id'),
    namespace text,
    object_id bigint,
    state integer,
    status_date timestamp with time zone,
    constraint workflow_status_company_pkey primary key (id),
    constraint workflow_status_company_state_fkey foreign key (state) references workflow_status_code (id)
);
alter sequence seq_workflow_status_company_id owned by workflow_status_company.id;
create unique index workflow_status_company_idx on workflow_status_company (namespace, object_id);

create table stock_metadata (
	date bigint,
	company_id bigint,
	market_open_minute integer,
	market_close_minute integer,
	interval integer,
	timezone_offset integer,
	exchange text,
	create_date timestamp with time zone,
	constraint stock_metadata_company_id_fkey foreign key (company_id) references company (id),
	constraint stock_metadata_pkey primary key (company_id, date)
);

create sequence seq_stock_price_daily_id;
create table stock_price_daily (
	id bigint default nextval('seq_stock_price_daily_id'),
	date bigint,
	company_id bigint,
	market_open_minute integer,
	market_close_minute integer,
	interval integer,
	timezone_offset integer,
	exchange text,
	price_list jsonb,
	create_date timestamp with time zone,
	update_date timestamp with time zone,
	constraint stock_price_daily_company_id_fkey foreign key (company_id) references company (id),
	constraint stock_price_daily_pkey primary key (id)
);
alter sequence seq_stock_price_daily_id owned by stock_price_daily.id;
create unique index stock_price_daily_idx on stock_price_daily (company_id, date);

--commit;
--rollback;





