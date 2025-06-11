const {onDocumentUpdated} = require("firebase-functions/v2/firestore");
const {logger} = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.onIncidentStatusChange = onDocumentUpdated(
    {
      document: "incidents/{incidentId}",
    },
    async (event) => {
      const before = event.data.before.data();
      const after = event.data.after.data();
      const incidentId = event.params.incidentId;
      if (!before || !after) {
        logger.warn("Datos 'before' o 'after' no disponibles");
        return;
      }

      if (before.status !== after.status) {
        const uid = after.user_id;

        if (!uid) {
          logger.warn(`No se encontró uid en el incidente ${incidentId}`);
          return;
        }

        logger.info(`Status del incidente ${incidentId}
           cambió de ${before.status} a ${after.status}. 
           Enviando notificación al usuario ${uid}`);

        try {
          const tokensSnapshot = await admin.firestore()
              .collection("device_tokens")
              .where("userId", "==", uid)
              .where("notification_activated", "==", true)
              .get();

          if (tokensSnapshot.empty) {
            logger.info(`No se encontraron tokens para el usuario ${uid}`);
            return;
          }

          const tokens = tokensSnapshot.docs
              .map((doc) => doc.data().token)
              .filter((token) => !!token);

          if (tokens.length === 0) {
            logger.info(`No hay tokens válidos para el usuario ${uid}`);
            return;
          }
          logger.info(`Tokens a los que se enviará la notificación: ${tokens}`);

          const message = {
            notification: {
              title: "Incidente actualizado",
              body: `El estado de un incidente cambió a: ${after.status}`,
            },
            tokens: tokens,
          };

          const response = await admin.
              messaging().
              sendEachForMulticast(message);
          logger.info("Notificación enviada con éxito:", response);
        } catch (error) {
          logger.error("Error enviando notificación:", error);
        }
      } else {
        logger.info("El status no cambió, no se hace nada.");
      }
    },
);
