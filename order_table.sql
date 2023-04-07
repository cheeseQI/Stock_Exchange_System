-- PostgreSQL database dump
--

-- Dumped from database version 12.14 (Ubuntu 12.14-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.14 (Ubuntu 12.14-0ubuntu0.20.04.1)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: order_table; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_table (
                                    id integer NOT NULL,
                                    symbol character varying(255) NOT NULL,
                                    amount numeric(20,8) NOT NULL,
                                    limit_price numeric(20,8) NOT NULL,
                                    account_id integer,
                                    status character varying(255) NOT NULL,
                                    "time" bigint NOT NULL,
                                    trans_id bigint,
                                    version integer DEFAULT 1
);


ALTER TABLE public.order_table OWNER TO postgres;

--
-- Name: order_table_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_table_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.order_table_id_seq OWNER TO postgres;

--
-- Name: order_table_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_table_id_seq OWNED BY public.order_table.id;


--
-- Name: order_table id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table ALTER COLUMN id SET DEFAULT nextval('public.order_table_id_seq'::regclass);


--
-- Name: order_table order_table_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT order_table_pkey PRIMARY KEY (id);


--
-- Name: order_table order_table_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_table
    ADD CONSTRAINT order_table_account_id_fkey FOREIGN KEY (account_id) REFERENCES public.account(account_id);


--
-- PostgreSQL database dump complete
--
