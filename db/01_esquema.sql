-- Esquema de la base de datos "uppa" (PostgreSQL). Generado con pg_dump --schema-only.
-- Uso: createdb -U postgres uppa && psql -U postgres -d uppa -f db/01_esquema.sql

--
-- PostgreSQL database dump
--


-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

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
-- Name: analisis; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.analisis (
    id_analisis integer NOT NULL,
    id_usuario integer NOT NULL,
    id_hongo integer NOT NULL,
    id_enfermedad integer,
    id_version integer NOT NULL,
    fecha timestamp without time zone NOT NULL,
    imagen_analizada character varying(500) NOT NULL,
    ubicacion character varying(255),
    precision_identificacion numeric(5,2),
    precision_diagnostico numeric(5,2)
);


--
-- Name: credencial; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.credencial (
    id_usuario integer NOT NULL,
    password_hash character varying(255) NOT NULL,
    salt character varying(100) NOT NULL,
    algoritmo_hash character varying(30) NOT NULL,
    mfa_habilitado boolean NOT NULL,
    mfa_secreto character varying(255),
    intentos_fallidos integer NOT NULL,
    bloqueado_hasta timestamp without time zone,
    ultimo_acceso timestamp without time zone,
    fecha_cambio_password timestamp without time zone,
    token_recuperacion character varying(255),
    expiracion_token timestamp without time zone
);


--
-- Name: enfermedad; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.enfermedad (
    id_enfermedad integer NOT NULL,
    nombre_cientifico character varying(150) NOT NULL,
    nombre_comun character varying(150) NOT NULL,
    informacion text
);


--
-- Name: enfermedad_id_enfermedad_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.enfermedad_id_enfermedad_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: enfermedad_id_enfermedad_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.enfermedad_id_enfermedad_seq OWNED BY public.enfermedad.id_enfermedad;


--
-- Name: hongo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hongo (
    id_hongo integer NOT NULL,
    familia character varying(100) NOT NULL,
    nombre_cientifico character varying(150) NOT NULL,
    nombre_comun character varying(150) NOT NULL,
    region character varying(100) NOT NULL,
    informacion text
);


--
-- Name: hongo_enfermedad; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hongo_enfermedad (
    id_hongo integer NOT NULL,
    id_enfermedad integer NOT NULL
);


--
-- Name: hongo_id_hongo_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.hongo_id_hongo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: hongo_id_hongo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.hongo_id_hongo_seq OWNED BY public.hongo.id_hongo;


--
-- Name: metodo_pago; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.metodo_pago (
    id_metodo_pago integer NOT NULL,
    id_usuario integer NOT NULL,
    tipo character varying(20) NOT NULL,
    token_pasarela character varying(255) NOT NULL,
    marca character varying(30),
    ultimos_cuatro character(4),
    fecha_expiracion character(7),
    es_predeterminado boolean NOT NULL,
    activo boolean NOT NULL
);


--
-- Name: pago; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pago (
    id_pago integer NOT NULL,
    id_suscripcion integer NOT NULL,
    id_metodo_pago integer NOT NULL,
    monto numeric(10,2) NOT NULL,
    moneda character(3) NOT NULL,
    fecha_pago timestamp without time zone NOT NULL,
    estado character varying(20) NOT NULL,
    referencia_pasarela character varying(100) NOT NULL,
    numero_factura character varying(50),
    descripcion text
);


--
-- Name: plan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.plan (
    id_plan integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text,
    precio numeric(10,2) NOT NULL,
    moneda character(3) NOT NULL,
    periodicidad character varying(20) NOT NULL,
    limite_analisis integer,
    activo boolean NOT NULL
);


--
-- Name: plan_id_plan_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.plan_id_plan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: plan_id_plan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.plan_id_plan_seq OWNED BY public.plan.id_plan;


--
-- Name: rol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rol (
    id_rol integer NOT NULL,
    nombre character varying(50) NOT NULL,
    descripcion text
);


--
-- Name: rol_id_rol_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.rol_id_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: rol_id_rol_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.rol_id_rol_seq OWNED BY public.rol.id_rol;


--
-- Name: suscripcion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.suscripcion (
    id_suscripcion integer NOT NULL,
    id_usuario integer NOT NULL,
    id_plan integer NOT NULL,
    fecha_inicio date NOT NULL,
    fecha_fin date,
    estado character varying(20) NOT NULL,
    renovacion_automatica boolean NOT NULL
);


--
-- Name: suscripcion_id_suscripcion_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.suscripcion_id_suscripcion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: suscripcion_id_suscripcion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.suscripcion_id_suscripcion_seq OWNED BY public.suscripcion.id_suscripcion;


--
-- Name: tratamiento; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tratamiento (
    id_tratamiento integer NOT NULL,
    id_enfermedad integer NOT NULL,
    requisitos text,
    triaje character varying(20),
    pasos text,
    fuente character varying(255)
);


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario (
    id_usuario integer NOT NULL,
    nombre character varying(150) NOT NULL,
    nombre_usuario character varying(100) NOT NULL,
    email character varying(255) NOT NULL,
    direccion character varying(255),
    telefono character varying(30),
    fecha_registro timestamp without time zone NOT NULL,
    estado character varying(20) NOT NULL,
    email_verificado boolean NOT NULL
);


--
-- Name: usuario_id_usuario_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuario_id_usuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuario_id_usuario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuario_id_usuario_seq OWNED BY public.usuario.id_usuario;


--
-- Name: usuario_rol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario_rol (
    id_usario integer NOT NULL,
    id_rol integer NOT NULL,
    fecha_asignacion timestamp without time zone NOT NULL
);


--
-- Name: version_modelo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.version_modelo (
    id_version integer NOT NULL,
    nombre character varying(150) NOT NULL,
    version character varying(30) NOT NULL,
    epocas_entrenamiento integer NOT NULL,
    tamano_dataset integer NOT NULL,
    precision_promedio numeric(5,2) NOT NULL
);


--
-- Name: version_modelo_id_version_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.version_modelo_id_version_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: version_modelo_id_version_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.version_modelo_id_version_seq OWNED BY public.version_modelo.id_version;


--
-- Name: enfermedad id_enfermedad; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enfermedad ALTER COLUMN id_enfermedad SET DEFAULT nextval('public.enfermedad_id_enfermedad_seq'::regclass);


--
-- Name: hongo id_hongo; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hongo ALTER COLUMN id_hongo SET DEFAULT nextval('public.hongo_id_hongo_seq'::regclass);


--
-- Name: plan id_plan; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.plan ALTER COLUMN id_plan SET DEFAULT nextval('public.plan_id_plan_seq'::regclass);


--
-- Name: rol id_rol; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol ALTER COLUMN id_rol SET DEFAULT nextval('public.rol_id_rol_seq'::regclass);


--
-- Name: suscripcion id_suscripcion; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripcion ALTER COLUMN id_suscripcion SET DEFAULT nextval('public.suscripcion_id_suscripcion_seq'::regclass);


--
-- Name: usuario id_usuario; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario ALTER COLUMN id_usuario SET DEFAULT nextval('public.usuario_id_usuario_seq'::regclass);


--
-- Name: version_modelo id_version; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.version_modelo ALTER COLUMN id_version SET DEFAULT nextval('public.version_modelo_id_version_seq'::regclass);


--
-- Name: analisis analisis_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analisis
    ADD CONSTRAINT analisis_pkey PRIMARY KEY (id_analisis, id_hongo, id_version);


--
-- Name: credencial credencial_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.credencial
    ADD CONSTRAINT credencial_pkey PRIMARY KEY (id_usuario);


--
-- Name: enfermedad enfermedad_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enfermedad
    ADD CONSTRAINT enfermedad_pkey PRIMARY KEY (id_enfermedad);


--
-- Name: hongo_enfermedad hongo_enfermedad_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hongo_enfermedad
    ADD CONSTRAINT hongo_enfermedad_pkey PRIMARY KEY (id_hongo, id_enfermedad);


--
-- Name: hongo hongo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hongo
    ADD CONSTRAINT hongo_pkey PRIMARY KEY (id_hongo);


--
-- Name: metodo_pago metodo_pago_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.metodo_pago
    ADD CONSTRAINT metodo_pago_pkey PRIMARY KEY (id_metodo_pago, id_usuario);


--
-- Name: pago pago_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT pago_pkey PRIMARY KEY (id_pago, id_suscripcion);


--
-- Name: plan plan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY (id_plan);


--
-- Name: rol rol_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (id_rol);


--
-- Name: suscripcion suscripcion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripcion
    ADD CONSTRAINT suscripcion_pkey PRIMARY KEY (id_suscripcion);


--
-- Name: tratamiento tratamiento_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tratamiento
    ADD CONSTRAINT tratamiento_pkey PRIMARY KEY (id_tratamiento, id_enfermedad);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario);


--
-- Name: usuario_rol usuario_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_pkey PRIMARY KEY (id_usario, id_rol);


--
-- Name: version_modelo version_modelo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.version_modelo
    ADD CONSTRAINT version_modelo_pkey PRIMARY KEY (id_version);


--
-- Name: hongo_enfermedad afecta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hongo_enfermedad
    ADD CONSTRAINT afecta FOREIGN KEY (id_enfermedad) REFERENCES public.enfermedad(id_enfermedad);


--
-- Name: usuario_rol agrupa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT agrupa FOREIGN KEY (id_rol) REFERENCES public.rol(id_rol);


--
-- Name: credencial autentica; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.credencial
    ADD CONSTRAINT autentica FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);


--
-- Name: hongo_enfermedad clasifica; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hongo_enfermedad
    ADD CONSTRAINT clasifica FOREIGN KEY (id_hongo) REFERENCES public.hongo(id_hongo);


--
-- Name: suscripcion contrata; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripcion
    ADD CONSTRAINT contrata FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);


--
-- Name: suscripcion define; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.suscripcion
    ADD CONSTRAINT define FOREIGN KEY (id_plan) REFERENCES public.plan(id_plan);


--
-- Name: analisis diagnostica; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analisis
    ADD CONSTRAINT diagnostica FOREIGN KEY (id_enfermedad) REFERENCES public.enfermedad(id_enfermedad);


--
-- Name: analisis ejecuta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analisis
    ADD CONSTRAINT ejecuta FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);


--
-- Name: pago genera; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT genera FOREIGN KEY (id_suscripcion) REFERENCES public.suscripcion(id_suscripcion);


--
-- Name: analisis identifica; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analisis
    ADD CONSTRAINT identifica FOREIGN KEY (id_hongo) REFERENCES public.hongo(id_hongo);


--
-- Name: usuario_rol posee; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT posee FOREIGN KEY (id_usario) REFERENCES public.usuario(id_usuario);


--
-- Name: analisis procesa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analisis
    ADD CONSTRAINT procesa FOREIGN KEY (id_version) REFERENCES public.version_modelo(id_version);


--
-- Name: metodo_pago registra; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.metodo_pago
    ADD CONSTRAINT registra FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);


--
-- Name: tratamiento trata; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tratamiento
    ADD CONSTRAINT trata FOREIGN KEY (id_enfermedad) REFERENCES public.enfermedad(id_enfermedad);


--
-- PostgreSQL database dump complete
--


