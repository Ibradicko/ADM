-- Nettoyage d'une base ADM de production apres initialisation avec des donnees de demonstration.
-- Objectif : supprimer les donnees metier et tous les comptes sauf le compte admin.
-- A executer dans PostgreSQL, sur la base ADM cible, apres sauvegarde.

BEGIN;

TRUNCATE TABLE
  regularisation_redevance,
  paiement_redevance,
  ligne_calcul_redevance,
  calcul_redevance,
  regle_redevance,
  rapport_export,
  journal_audit,
  ligne_transfert_stock,
  transfert_stock,
  ligne_inventaire_stock,
  inventaire_stock,
  ligne_reception_produit,
  reception_produit,
  ligne_mouvement_stock,
  mouvement_stock,
  stock_produit,
  depot_stock,
  ticket_caisse,
  operation_corrective_vente,
  paiement_vente,
  ligne_vente,
  vente,
  scan_inconnu,
  etiquette_produit,
  lot_etiquettes,
  historique_code_barres,
  code_barres_produit,
  tarif_produit,
  produit,
  sous_famille_article,
  famille_article,
  groupe_article,
  affectation_utilisateur,
  exploitation_boutique,
  boutique,
  locataire
RESTART IDENTITY CASCADE;

DELETE FROM jhi_user_authority
WHERE user_id NOT IN (SELECT id FROM jhi_user WHERE login = 'admin');

DELETE FROM jhi_user
WHERE login <> 'admin';

UPDATE jhi_user
SET
  activated = true,
  must_change_password = false,
  last_modified_by = 'cleanup-production',
  last_modified_date = now()
WHERE login = 'admin';

SELECT setval('sequence_generator', GREATEST((SELECT last_value FROM sequence_generator), 1000), true);

COMMIT;
