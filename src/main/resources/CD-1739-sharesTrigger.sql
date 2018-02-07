ALTER TABLE shares.client
   ADD COLUMN row_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp();
;

ALTER TABLE shares."order"
   ADD COLUMN row_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp();
;

ALTER TABLE shares.registration
   ADD COLUMN row_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp();
;

ALTER TABLE shares.address
   ADD COLUMN row_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp();
;

ALTER TABLE shares.cnote
   ADD COLUMN row_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp();
;

ALTER TABLE "cli-hold"
   ADD COLUMN row_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp();
;

CREATE OR REPLACE FUNCTION update_row_modified_function()
RETURNS TRIGGER
AS
$$
BEGIN
    -- ASSUMES the table has a column named exactly "row_modified_".
    -- Fetch date-time of actual current moment from clock, rather than start of statement or start of transaction.
    NEW.row_modified = clock_timestamp();
    RETURN NEW;
END;
$$
language 'plpgsql';

CREATE TRIGGER row_mod_on_client_trigger
BEFORE UPDATE
ON client
FOR EACH ROW
EXECUTE PROCEDURE update_row_modified_function();


CREATE TRIGGER row_mod_on_order_trigger
BEFORE UPDATE
ON "order"
FOR EACH ROW
EXECUTE PROCEDURE update_row_modified_function();


CREATE TRIGGER row_mod_on_registration_trigger
BEFORE UPDATE
ON registration
FOR EACH ROW
EXECUTE PROCEDURE update_row_modified_function();

CREATE TRIGGER row_mod_on_address_trigger
BEFORE UPDATE
ON address
FOR EACH ROW
EXECUTE PROCEDURE update_row_modified_function();

CREATE TRIGGER row_mod_on_cnote_trigger
BEFORE UPDATE
ON cnote
FOR EACH ROW
EXECUTE PROCEDURE update_row_modified_function();

CREATE TRIGGER row_mod_on_holding_trigger
BEFORE UPDATE
ON "cli-hold"
FOR EACH ROW
EXECUTE PROCEDURE update_row_modified_function();
