CREATE TABLE "FOO" (
	"FOO_ID" IDENTITY NOT NULL,
	"FOO_FNAME" VARCHAR(100) NOT NULL,
	"FOO_LNAME" VARCHAR(100),
	"FOO_RATE" DECIMAL(4, 1),
	"FOO_ADD_DATE" TIMESTAMP,
	CONSTRAINT "PK_STUDENTS" PRIMARY KEY ("FOO_ID")
);

--PostgreSQL
/*
CREATE TABLE foo
(
  foo_id serial NOT NULL,
  foo_lname character varying(30),
  foo_fname character varying(30),
  foo_rate numeric(4,1),
  foo_add_date timestamp without time zone,
  CONSTRAINT foo_pkey PRIMARY KEY (foo_id)
)
*/