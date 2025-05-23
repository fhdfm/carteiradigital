-- TRIGGER PRODUTO
-- ==========================
CREATE OR REPLACE FUNCTION trg_item_pedido_quantidade_vendida()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    ---------------------------------------------------------------------------
    -- INSERT → soma a quantidade, se o pedido é CONCLUIDO
    ---------------------------------------------------------------------------
    IF TG_OP = 'INSERT' THEN
        IF EXISTS (
            SELECT 1 FROM pedido p
             WHERE p.id = NEW.pedido_id
               AND p.status = 'CONCLUIDO'
        ) THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida + NEW.quantidade
WHERE id = NEW.produto_id;
END IF;
RETURN NEW;

---------------------------------------------------------------------------
-- UPDATE → subtrai o “antigo” e soma o “novo” **apenas** quando cada lado
--          estiver vinculado a um pedido CONCLUIDO.
---------------------------------------------------------------------------
ELSIF TG_OP = 'UPDATE' THEN
        -- Remove efeito anterior, se era contabilizado
        IF EXISTS (
            SELECT 1 FROM pedido p
             WHERE p.id = OLD.pedido_id
               AND p.status = 'CONCLUIDO'
        ) THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida - OLD.quantidade
WHERE id = OLD.produto_id;
END IF;

        -- Aplica novo efeito, se deve ser contabilizado
        IF EXISTS (
            SELECT 1 FROM pedido p
             WHERE p.id = NEW.pedido_id
               AND p.status = 'CONCLUIDO'
        ) THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida + NEW.quantidade
WHERE id = NEW.produto_id;
END IF;

RETURN NEW;

---------------------------------------------------------------------------
-- DELETE → só subtrai se o pedido era CONCLUIDO
---------------------------------------------------------------------------
ELSIF TG_OP = 'DELETE' THEN
        IF EXISTS (
            SELECT 1 FROM pedido p
             WHERE p.id = OLD.pedido_id
               AND p.status = 'CONCLUIDO'
        ) THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida - OLD.quantidade
WHERE id = OLD.produto_id;
END IF;
RETURN OLD;
END IF;
END;
$$;

-------------------------------------------------------------------------------
-- TRIGGER: executa a função para INSERT/UPDATE/DELETE em item_pedido
-------------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_item_pedido_quantidade_vendida
    ON item_pedido;

CREATE TRIGGER trg_item_pedido_quantidade_vendida
    AFTER INSERT OR UPDATE OR DELETE
                    ON item_pedido
                        FOR EACH ROW
                        EXECUTE FUNCTION trg_item_pedido_quantidade_vendida();

-------------------------------------------------------------------------------
-- FUNÇÃO: ajusta quantidade_vendida quando o status do pedido muda
-------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION trg_pedido_status_quantidade_vendida()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
rec RECORD;
    v_factor INTEGER;   -- +1 se está concluindo, -1 se está “des-concluindo”
BEGIN
    ---------------------------------------------------------------------------
    -- Caso 1: virou CONCLUIDO (contabiliza venda)
    ---------------------------------------------------------------------------
    IF OLD.status <> 'CONCLUIDO' AND NEW.status = 'CONCLUIDO' THEN
        v_factor := +1;

    ---------------------------------------------------------------------------
    -- Caso 2: saiu de CONCLUIDO (estorno)
    ---------------------------------------------------------------------------
    ELSIF OLD.status = 'CONCLUIDO' AND NEW.status <> 'CONCLUIDO' THEN
        v_factor := -1;

ELSE
        -- Qualquer outra mudança de status não importa
        RETURN NEW;
END IF;

    -- Atualiza todos os produtos do pedido em lote
FOR rec IN
SELECT produto_id, quantidade
FROM item_pedido
WHERE pedido_id = NEW.id
    LOOP
UPDATE produto
SET quantidade_vendida = quantidade_vendida + v_factor * rec.quantidade
WHERE id = rec.produto_id;
END LOOP;

RETURN NEW;
END;
$$;

-------------------------------------------------------------------------------
-- TRIGGER: executa a função sempre que status mudar
-------------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_pedido_status_quantidade_vendida
    ON pedido;

CREATE TRIGGER trg_pedido_status_quantidade_vendida
    AFTER UPDATE OF status                 -- executa só quando a coluna status muda
    ON pedido
    FOR EACH ROW
    EXECUTE FUNCTION trg_pedido_status_quantidade_vendida();


-- ==========================

