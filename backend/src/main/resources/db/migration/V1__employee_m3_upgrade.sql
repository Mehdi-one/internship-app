CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    qualification VARCHAR(255) NOT NULL,
    contract_type VARCHAR(50) NOT NULL,
    hourly_cost NUMERIC(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE employees ADD COLUMN IF NOT EXISTS qualification VARCHAR(255);
ALTER TABLE employees ADD COLUMN IF NOT EXISTS contract_type VARCHAR(50);
ALTER TABLE employees ADD COLUMN IF NOT EXISTS hourly_cost NUMERIC(15, 2);
ALTER TABLE employees ADD COLUMN IF NOT EXISTS status VARCHAR(50);
ALTER TABLE employees ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE employees
SET qualification = 'Non renseignee'
WHERE qualification IS NULL OR BTRIM(qualification) = '';

UPDATE employees
SET contract_type = CASE contract_type
    WHEN 'INTERIM' THEN 'INTERIMAIRE'
    WHEN 'FREELANCE' THEN 'SOUS_TRAITANT'
    WHEN 'OTHER' THEN 'SOUS_TRAITANT'
    ELSE COALESCE(contract_type, 'CDD')
END;

UPDATE employees SET hourly_cost = 0 WHERE hourly_cost IS NULL;
UPDATE employees SET status = 'ACTIVE' WHERE status IS NULL;
UPDATE employees SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE employees SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

ALTER TABLE employees ALTER COLUMN qualification SET NOT NULL;
ALTER TABLE employees ALTER COLUMN contract_type SET NOT NULL;
ALTER TABLE employees ALTER COLUMN hourly_cost SET NOT NULL;
ALTER TABLE employees ALTER COLUMN status SET NOT NULL;
ALTER TABLE employees ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE employees ALTER COLUMN updated_at SET NOT NULL;

CREATE TABLE IF NOT EXISTS employee_cost_history (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    hourly_cost NUMERIC(15, 2) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_employee_cost_history_lookup
    ON employee_cost_history(employee_id, effective_date DESC, created_at DESC);

DO $$
BEGIN
    IF to_regclass('public.employee_hourly_cost_history') IS NOT NULL THEN
        EXECUTE '
            INSERT INTO employee_cost_history (employee_id, hourly_cost, effective_date, created_at)
            SELECT old.employee_id, old.hourly_cost, old.effective_date, old.created_at
            FROM employee_hourly_cost_history old
            WHERE NOT EXISTS (
                SELECT 1
                FROM employee_cost_history current
                WHERE current.employee_id = old.employee_id
                  AND current.hourly_cost = old.hourly_cost
                  AND current.effective_date = old.effective_date
                  AND current.created_at = old.created_at
            )';
    END IF;
END $$;

INSERT INTO employee_cost_history (employee_id, hourly_cost, effective_date, created_at)
SELECT employee.id, employee.hourly_cost, CURRENT_DATE, CURRENT_TIMESTAMP
FROM employees employee
WHERE NOT EXISTS (
    SELECT 1
    FROM employee_cost_history history
    WHERE history.employee_id = employee.id
);

DROP TABLE IF EXISTS employee_hourly_cost_history;
