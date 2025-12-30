--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2
-- Dumped by pg_dump version 15.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: ctrm; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA ctrm;


ALTER SCHEMA ctrm OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: commodity_option_instrument; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.commodity_option_instrument (
    expiry_date date,
    option_type character varying(255),
    strike_price numeric(38,2),
    id bigint NOT NULL
);


ALTER TABLE ctrm.commodity_option_instrument OWNER TO ctrm_user;

--
-- Name: commodity_swap_instrument; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.commodity_swap_instrument (
    end_date date,
    fixed_price numeric(38,2),
    floating_price_index character varying(255),
    start_date date,
    id bigint NOT NULL
);


ALTER TABLE ctrm.commodity_swap_instrument OWNER TO ctrm_user;

--
-- Name: credit_limit; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.credit_limit (
    id bigint NOT NULL,
    counterparty character varying(255),
    limit_amount numeric(38,2)
);


ALTER TABLE ctrm.credit_limit OWNER TO ctrm_user;

--
-- Name: credit_limit_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.credit_limit_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.credit_limit_seq OWNER TO ctrm_user;

--
-- Name: deal_templates; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.deal_templates (
    id bigint NOT NULL,
    auto_approval_allowed boolean NOT NULL,
    commodity character varying(255),
    currency character varying(255),
    default_price numeric(38,2),
    default_quantity numeric(38,2),
    instrument_type character varying(255),
    mtm_approval_threshold numeric(38,2),
    pricing_model character varying(255),
    template_name character varying(255) NOT NULL,
    unit character varying(255),
    instrument_id bigint NOT NULL
);


ALTER TABLE ctrm.deal_templates OWNER TO ctrm_user;

--
-- Name: deal_templates_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.deal_templates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.deal_templates_id_seq OWNER TO ctrm_user;

--
-- Name: deal_templates_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.deal_templates_id_seq OWNED BY ctrm.deal_templates.id;


--
-- Name: forward_curves; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.forward_curves (
    id bigint NOT NULL,
    delivery_date date NOT NULL,
    price double precision NOT NULL,
    instrument_id bigint NOT NULL
);


ALTER TABLE ctrm.forward_curves OWNER TO ctrm_user;

--
-- Name: forward_curves_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.forward_curves_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.forward_curves_id_seq OWNER TO ctrm_user;

--
-- Name: forward_curves_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.forward_curves_id_seq OWNED BY ctrm.forward_curves.id;


--
-- Name: gas_forward_instrument; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.gas_forward_instrument (
    delivery_date date,
    id bigint NOT NULL
);


ALTER TABLE ctrm.gas_forward_instrument OWNER TO ctrm_user;

--
-- Name: instruments; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.instruments (
    id bigint NOT NULL,
    commodity character varying(255),
    currency character varying(255),
    instrument_code character varying(255) NOT NULL,
    instrument_type character varying(255),
    unit character varying(255),
    CONSTRAINT instruments_instrument_type_check CHECK (((instrument_type)::text = ANY ((ARRAY['POWER_FORWARD'::character varying, 'RENEWABLE_PPA'::character varying, 'GAS_FORWARD'::character varying, 'COMMODITY_SWAP'::character varying, 'OPTION'::character varying, 'FREIGHT'::character varying])::text[])))
);


ALTER TABLE ctrm.instruments OWNER TO ctrm_user;

--
-- Name: instruments_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.instruments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.instruments_id_seq OWNER TO ctrm_user;

--
-- Name: instruments_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.instruments_id_seq OWNED BY ctrm.instruments.id;


--
-- Name: lifecycle_rules; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.lifecycle_rules (
    id bigint NOT NULL,
    approval_role character varying(255),
    auto_approve boolean NOT NULL,
    desk character varying(255) NOT NULL,
    effective_from date,
    effective_to date,
    enabled boolean NOT NULL,
    event character varying(255) NOT NULL,
    event_type character varying(255) NOT NULL,
    from_state character varying(255),
    from_status character varying(255) NOT NULL,
    max_occurrence integer NOT NULL,
    name character varying(255),
    production_enabled boolean NOT NULL,
    to_state character varying(255),
    to_status character varying(255) NOT NULL,
    version integer NOT NULL,
    CONSTRAINT lifecycle_rules_event_type_check CHECK (((event_type)::text = ANY ((ARRAY['CREATED'::character varying, 'AMENDED'::character varying, 'PRICED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[]))),
    CONSTRAINT lifecycle_rules_from_state_check CHECK (((from_state)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[]))),
    CONSTRAINT lifecycle_rules_from_status_check CHECK (((from_status)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[]))),
    CONSTRAINT lifecycle_rules_to_state_check CHECK (((to_state)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[]))),
    CONSTRAINT lifecycle_rules_to_status_check CHECK (((to_status)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[])))
);


ALTER TABLE ctrm.lifecycle_rules OWNER TO ctrm_user;

--
-- Name: lifecycle_rules_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.lifecycle_rules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.lifecycle_rules_id_seq OWNER TO ctrm_user;

--
-- Name: lifecycle_rules_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.lifecycle_rules_id_seq OWNED BY ctrm.lifecycle_rules.id;


--
-- Name: market_prices; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.market_prices (
    id bigint NOT NULL,
    instrument_code character varying(255) NOT NULL,
    price numeric(38,2) NOT NULL
);


ALTER TABLE ctrm.market_prices OWNER TO ctrm_user;

--
-- Name: market_prices_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.market_prices_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.market_prices_id_seq OWNER TO ctrm_user;

--
-- Name: market_prices_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.market_prices_id_seq OWNED BY ctrm.market_prices.id;


--
-- Name: portfolio_positions; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.portfolio_positions (
    id bigint NOT NULL,
    net_quantity numeric(38,2) NOT NULL,
    portfolio character varying(255) NOT NULL,
    instrument_id bigint NOT NULL
);


ALTER TABLE ctrm.portfolio_positions OWNER TO ctrm_user;

--
-- Name: portfolio_positions_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.portfolio_positions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.portfolio_positions_id_seq OWNER TO ctrm_user;

--
-- Name: portfolio_positions_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.portfolio_positions_id_seq OWNED BY ctrm.portfolio_positions.id;


--
-- Name: position; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm."position" (
    id bigint NOT NULL,
    net_quantity double precision NOT NULL,
    portfolio character varying(255),
    instrument_id bigint
);


ALTER TABLE ctrm."position" OWNER TO ctrm_user;

--
-- Name: position_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.position_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.position_id_seq OWNER TO ctrm_user;

--
-- Name: position_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.position_id_seq OWNED BY ctrm."position".id;


--
-- Name: power_forward_instrument; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.power_forward_instrument (
    end_date date,
    start_date date,
    id bigint NOT NULL
);


ALTER TABLE ctrm.power_forward_instrument OWNER TO ctrm_user;

--
-- Name: renewableppainstrument; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.renewableppainstrument (
    forecast_curve character varying(255),
    settlement_type character varying(255),
    technology character varying(255),
    id bigint NOT NULL
);


ALTER TABLE ctrm.renewableppainstrument OWNER TO ctrm_user;

--
-- Name: trade_events; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.trade_events (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    event_type character varying(255) NOT NULL,
    source character varying(255) NOT NULL,
    triggered_by character varying(255) NOT NULL,
    trade_id bigint NOT NULL,
    CONSTRAINT trade_events_event_type_check CHECK (((event_type)::text = ANY ((ARRAY['CREATED'::character varying, 'AMENDED'::character varying, 'PRICED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[])))
);


ALTER TABLE ctrm.trade_events OWNER TO ctrm_user;

--
-- Name: trade_events_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.trade_events_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.trade_events_id_seq OWNER TO ctrm_user;

--
-- Name: trade_events_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.trade_events_id_seq OWNED BY ctrm.trade_events.id;


--
-- Name: trade_lifecycle_rules; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.trade_lifecycle_rules (
    id bigint NOT NULL,
    auto boolean NOT NULL,
    current_status character varying(255) NOT NULL,
    event_type character varying(255) NOT NULL,
    max_occurrence integer NOT NULL,
    next_status character varying(255) NOT NULL,
    requires_approval boolean NOT NULL,
    CONSTRAINT trade_lifecycle_rules_current_status_check CHECK (((current_status)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[]))),
    CONSTRAINT trade_lifecycle_rules_event_type_check CHECK (((event_type)::text = ANY ((ARRAY['CREATED'::character varying, 'AMENDED'::character varying, 'PRICED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[]))),
    CONSTRAINT trade_lifecycle_rules_next_status_check CHECK (((next_status)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[])))
);


ALTER TABLE ctrm.trade_lifecycle_rules OWNER TO ctrm_user;

--
-- Name: trade_lifecycle_rules_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.trade_lifecycle_rules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.trade_lifecycle_rules_id_seq OWNER TO ctrm_user;

--
-- Name: trade_lifecycle_rules_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.trade_lifecycle_rules_id_seq OWNED BY ctrm.trade_lifecycle_rules.id;


--
-- Name: trades; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.trades (
    id bigint NOT NULL,
    buy_sell character varying(255) NOT NULL,
    counterparty character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    pending_approval_role character varying(255),
    portfolio character varying(255) NOT NULL,
    price numeric(38,2) NOT NULL,
    quantity numeric(38,2) NOT NULL,
    status character varying(255) NOT NULL,
    template_id bigint,
    trade_id character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    instrument_id bigint NOT NULL,
    CONSTRAINT trades_buy_sell_check CHECK (((buy_sell)::text = ANY ((ARRAY['BUY'::character varying, 'SELL'::character varying])::text[]))),
    CONSTRAINT trades_status_check CHECK (((status)::text = ANY ((ARRAY['CREATED'::character varying, 'VALIDATED'::character varying, 'BOOKED'::character varying, 'PRICED'::character varying, 'CONFIRMED'::character varying, 'DELIVERED'::character varying, 'INVOICED'::character varying, 'SETTLED'::character varying, 'CANCELLED'::character varying, 'AMENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'APPROVED'::character varying])::text[])))
);


ALTER TABLE ctrm.trades OWNER TO ctrm_user;

--
-- Name: trades_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.trades_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.trades_id_seq OWNER TO ctrm_user;

--
-- Name: trades_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.trades_id_seq OWNED BY ctrm.trades.id;


--
-- Name: valuation_history; Type: TABLE; Schema: ctrm; Owner: ctrm_user
--

CREATE TABLE ctrm.valuation_history (
    id bigint NOT NULL,
    mtm numeric(38,2),
    valuation_date date,
    trade_id bigint NOT NULL
);


ALTER TABLE ctrm.valuation_history OWNER TO ctrm_user;

--
-- Name: valuation_history_id_seq; Type: SEQUENCE; Schema: ctrm; Owner: ctrm_user
--

CREATE SEQUENCE ctrm.valuation_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ctrm.valuation_history_id_seq OWNER TO ctrm_user;

--
-- Name: valuation_history_id_seq; Type: SEQUENCE OWNED BY; Schema: ctrm; Owner: ctrm_user
--

ALTER SEQUENCE ctrm.valuation_history_id_seq OWNED BY ctrm.valuation_history.id;


--
-- Name: deal_templates id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.deal_templates ALTER COLUMN id SET DEFAULT nextval('ctrm.deal_templates_id_seq'::regclass);


--
-- Name: forward_curves id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.forward_curves ALTER COLUMN id SET DEFAULT nextval('ctrm.forward_curves_id_seq'::regclass);


--
-- Name: instruments id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.instruments ALTER COLUMN id SET DEFAULT nextval('ctrm.instruments_id_seq'::regclass);


--
-- Name: lifecycle_rules id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.lifecycle_rules ALTER COLUMN id SET DEFAULT nextval('ctrm.lifecycle_rules_id_seq'::regclass);


--
-- Name: market_prices id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.market_prices ALTER COLUMN id SET DEFAULT nextval('ctrm.market_prices_id_seq'::regclass);


--
-- Name: portfolio_positions id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.portfolio_positions ALTER COLUMN id SET DEFAULT nextval('ctrm.portfolio_positions_id_seq'::regclass);


--
-- Name: position id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm."position" ALTER COLUMN id SET DEFAULT nextval('ctrm.position_id_seq'::regclass);


--
-- Name: trade_events id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trade_events ALTER COLUMN id SET DEFAULT nextval('ctrm.trade_events_id_seq'::regclass);


--
-- Name: trade_lifecycle_rules id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trade_lifecycle_rules ALTER COLUMN id SET DEFAULT nextval('ctrm.trade_lifecycle_rules_id_seq'::regclass);


--
-- Name: trades id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trades ALTER COLUMN id SET DEFAULT nextval('ctrm.trades_id_seq'::regclass);


--
-- Name: valuation_history id; Type: DEFAULT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.valuation_history ALTER COLUMN id SET DEFAULT nextval('ctrm.valuation_history_id_seq'::regclass);


--
-- Name: commodity_option_instrument commodity_option_instrument_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.commodity_option_instrument
    ADD CONSTRAINT commodity_option_instrument_pkey PRIMARY KEY (id);


--
-- Name: commodity_swap_instrument commodity_swap_instrument_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.commodity_swap_instrument
    ADD CONSTRAINT commodity_swap_instrument_pkey PRIMARY KEY (id);


--
-- Name: credit_limit credit_limit_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.credit_limit
    ADD CONSTRAINT credit_limit_pkey PRIMARY KEY (id);


--
-- Name: deal_templates deal_templates_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.deal_templates
    ADD CONSTRAINT deal_templates_pkey PRIMARY KEY (id);


--
-- Name: forward_curves forward_curves_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.forward_curves
    ADD CONSTRAINT forward_curves_pkey PRIMARY KEY (id);


--
-- Name: gas_forward_instrument gas_forward_instrument_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.gas_forward_instrument
    ADD CONSTRAINT gas_forward_instrument_pkey PRIMARY KEY (id);


--
-- Name: instruments instruments_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.instruments
    ADD CONSTRAINT instruments_pkey PRIMARY KEY (id);


--
-- Name: lifecycle_rules lifecycle_rules_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.lifecycle_rules
    ADD CONSTRAINT lifecycle_rules_pkey PRIMARY KEY (id);


--
-- Name: market_prices market_prices_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.market_prices
    ADD CONSTRAINT market_prices_pkey PRIMARY KEY (id);


--
-- Name: portfolio_positions portfolio_positions_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.portfolio_positions
    ADD CONSTRAINT portfolio_positions_pkey PRIMARY KEY (id);


--
-- Name: position position_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm."position"
    ADD CONSTRAINT position_pkey PRIMARY KEY (id);


--
-- Name: power_forward_instrument power_forward_instrument_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.power_forward_instrument
    ADD CONSTRAINT power_forward_instrument_pkey PRIMARY KEY (id);


--
-- Name: renewableppainstrument renewableppainstrument_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.renewableppainstrument
    ADD CONSTRAINT renewableppainstrument_pkey PRIMARY KEY (id);


--
-- Name: trade_events trade_events_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trade_events
    ADD CONSTRAINT trade_events_pkey PRIMARY KEY (id);


--
-- Name: trade_lifecycle_rules trade_lifecycle_rules_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trade_lifecycle_rules
    ADD CONSTRAINT trade_lifecycle_rules_pkey PRIMARY KEY (id);


--
-- Name: trades trades_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trades
    ADD CONSTRAINT trades_pkey PRIMARY KEY (id);


--
-- Name: deal_templates uk_4plhsumsj2hu8fas40no63118; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.deal_templates
    ADD CONSTRAINT uk_4plhsumsj2hu8fas40no63118 UNIQUE (template_name);


--
-- Name: trades uk_8d1lubanjnkryvjsjpqol15qh; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trades
    ADD CONSTRAINT uk_8d1lubanjnkryvjsjpqol15qh UNIQUE (trade_id);


--
-- Name: instruments uk_9sq9cn5em949bxp1nx3x32n7a; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.instruments
    ADD CONSTRAINT uk_9sq9cn5em949bxp1nx3x32n7a UNIQUE (instrument_code);


--
-- Name: valuation_history valuation_history_pkey; Type: CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.valuation_history
    ADD CONSTRAINT valuation_history_pkey PRIMARY KEY (id);


--
-- Name: trades fk3c9vcemmfh75a99xgrrbxmlq3; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trades
    ADD CONSTRAINT fk3c9vcemmfh75a99xgrrbxmlq3 FOREIGN KEY (instrument_id) REFERENCES ctrm.instruments(id);


--
-- Name: commodity_option_instrument fk4vl8u8rdqbbla0tfqtb8842w2; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.commodity_option_instrument
    ADD CONSTRAINT fk4vl8u8rdqbbla0tfqtb8842w2 FOREIGN KEY (id) REFERENCES ctrm.instruments(id);


--
-- Name: position fk54bfcfb6iexhkn1da2b8yeyuw; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm."position"
    ADD CONSTRAINT fk54bfcfb6iexhkn1da2b8yeyuw FOREIGN KEY (instrument_id) REFERENCES ctrm.instruments(id);


--
-- Name: power_forward_instrument fk6y3bbh38u9v1b6odycf1mc3oq; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.power_forward_instrument
    ADD CONSTRAINT fk6y3bbh38u9v1b6odycf1mc3oq FOREIGN KEY (id) REFERENCES ctrm.instruments(id);


--
-- Name: trade_events fkbp134hn7x50b8sv9ll4vnppvq; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.trade_events
    ADD CONSTRAINT fkbp134hn7x50b8sv9ll4vnppvq FOREIGN KEY (trade_id) REFERENCES ctrm.trades(id);


--
-- Name: valuation_history fkc6vsfhj0a0hcf32s4gqjv9fqq; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.valuation_history
    ADD CONSTRAINT fkc6vsfhj0a0hcf32s4gqjv9fqq FOREIGN KEY (trade_id) REFERENCES ctrm.trades(id);


--
-- Name: portfolio_positions fkfw19bqihf380tlqve0vloc4fh; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.portfolio_positions
    ADD CONSTRAINT fkfw19bqihf380tlqve0vloc4fh FOREIGN KEY (instrument_id) REFERENCES ctrm.instruments(id);


--
-- Name: deal_templates fkhl99wvxmea8u9ggxrcv9l89bi; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.deal_templates
    ADD CONSTRAINT fkhl99wvxmea8u9ggxrcv9l89bi FOREIGN KEY (instrument_id) REFERENCES ctrm.instruments(id);


--
-- Name: forward_curves fkiiun9rvscngheg65qvfunvc7p; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.forward_curves
    ADD CONSTRAINT fkiiun9rvscngheg65qvfunvc7p FOREIGN KEY (instrument_id) REFERENCES ctrm.instruments(id);


--
-- Name: gas_forward_instrument fkk1d589igro3er7y9cv0tp3lrw; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.gas_forward_instrument
    ADD CONSTRAINT fkk1d589igro3er7y9cv0tp3lrw FOREIGN KEY (id) REFERENCES ctrm.instruments(id);


--
-- Name: renewableppainstrument fkpqpstki1m78mgiy6i00m3b16q; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.renewableppainstrument
    ADD CONSTRAINT fkpqpstki1m78mgiy6i00m3b16q FOREIGN KEY (id) REFERENCES ctrm.instruments(id);


--
-- Name: commodity_swap_instrument fkstyl5oox8jm742wo8x05kduh6; Type: FK CONSTRAINT; Schema: ctrm; Owner: ctrm_user
--

ALTER TABLE ONLY ctrm.commodity_swap_instrument
    ADD CONSTRAINT fkstyl5oox8jm742wo8x05kduh6 FOREIGN KEY (id) REFERENCES ctrm.instruments(id);


--
-- Name: SCHEMA ctrm; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA ctrm TO ctrm_user;


--
-- PostgreSQL database dump complete
--

