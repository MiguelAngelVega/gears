CREATE TABLE "dis_students" (
	"std_id" IDENTITY NOT NULL,
	"std_fname" VARCHAR(100),
	"std_lname" VARCHAR(100),
	"std_add_date" TIMESTAMP,
	CONSTRAINT "pk_students" PRIMARY KEY ("std_id")
);