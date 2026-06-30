CREATE TABLE fuel_log (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL REFERENCES equipment(id),
    date DATE NOT NULL,
    liters NUMERIC(10, 2) NOT NULL,
    cost_per_liter NUMERIC(10, 2) NOT NULL,
    total_cost NUMERIC(10, 2) NOT NULL,
    mileage_or_hours NUMERIC(10, 2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_fuel_log_equipment_id ON fuel_log(equipment_id);
