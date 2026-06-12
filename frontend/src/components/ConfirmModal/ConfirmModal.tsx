import type { ReactNode, KeyboardEvent } from "react";
import styles from "./ConfirmModal.module.scss";

interface ConfirmModalProps {
  visible: boolean;
  title?: string;
  message: ReactNode;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

const ConfirmModal = ({
                        visible,
                        title = "Potwierdź akcję",
                        message,
                        confirmLabel = "Potwierdź",
                        cancelLabel = "Anuluj",
                        onConfirm,
                        onCancel,
                      }: ConfirmModalProps) => {
  if (!visible) return null;

  const handleOverlayKeyDown = (e: KeyboardEvent<HTMLDivElement>) => {
    if (e.key === "Escape" || e.key === "Enter" || e.key === " ") {
      onCancel();
    }
  };

  const handleModalKeyDown = (e: KeyboardEvent<HTMLDivElement>) => {
    e.stopPropagation();
  };

  return (
      <div
          className={styles.modalOverlay}
          role="presentation"
          onClick={onCancel}
          onKeyDown={handleOverlayKeyDown}
      >
        <div
            className={styles.modal}
            role="dialog"
            aria-modal="true"
            aria-labelledby="confirm-modal-title"
            onClick={(event) => event.stopPropagation()}
            onKeyDown={handleModalKeyDown}
        >
          <h3 id="confirm-modal-title">{title}</h3>
          <p>{message}</p>
          <div className={styles.actions}>
            <button type="button" className={styles.cancel} onClick={onCancel}>
              {cancelLabel}
            </button>
            <button type="button" className={styles.confirm} onClick={onConfirm}>
              {confirmLabel}
            </button>
          </div>
        </div>
      </div>
  );
};

export default ConfirmModal;