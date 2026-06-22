CREATE TYPE feedback_status AS ENUM (
  'PENDENTE',
  'APROVADO',
  'REJEITADO'
);

ALTER TABLE feedbacks
ADD COLUMN status feedback_status NOT NULL DEFAULT 'PENDENTE';