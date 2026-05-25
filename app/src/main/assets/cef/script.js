// ================= ALERT =================

function createAlert(title, message, callback) {

    document.getElementById('alert-title').innerText = title;
    document.getElementById('alert-message').innerText = message;

    const overlay = document.getElementById('alert-overlay');

    overlay.classList.remove('hidden');

    document.getElementById('okay-button').onclick = () => {
        hideAlert();
        callback(true);
    };

    document.getElementById('cancel-button').onclick = () => {
        hideAlert();
        callback(false);
    };
}

function hideAlert() {
    const overlay = document.getElementById('alert-overlay');
    overlay.classList.add('hidden');
}

function showAlert(eventData) {
    const eventDataJson = JSON.parse(eventData);

    const alertId = parseInt(eventDataJson[0]);
    const alertTitle = eventDataJson[1];
    const alertMessage = eventDataJson[2];

    createAlert(alertTitle, alertMessage, (response) => {
        let outgoingEventData = new Array();

        outgoingEventData.push(alertId);
        outgoingEventData.push(response);

        console.log(`Response status: ${response}`);

        Cef.sendEvent(
            "alert_response",
            JSON.stringify(outgoingEventData)
        );
    });
}

/* ================= TOAST NOTIFICATION ================= */

function createToast(type, title, message) {
    const container = document.getElementById("toast-container");
    const toast = document.createElement("div");

    toast.classList.add("toast");
    toast.classList.add(type);

    toast.innerHTML = `
        <div class="toast-title">${title}</div>
        <div class="toast-message">${message}</div>
    `;

    container.appendChild(toast);

    // Auto remove
    setTimeout(() => {
        toast.classList.add("hide");

        setTimeout(() => {
            toast.remove();
        }, 400);

    }, 5000);
}

function showNotification(eventData) {
    const data = JSON.parse(eventData);

    const type = data[0];     // Lấy loại thông báo: success, error, warning, info
    const message = data[2];  // Lấy nội dung thông báo từ chuỗi Pawn gửi qua

    // Tự động đặt lại Tiêu đề viết hoa dựa theo từng Loại (Type)
    let title = "THÔNG BÁO";
    if (type === "error") {
        title = "ERROR";
    } else if (type === "warning") {
        title = "WARNING";
    } else if (type === "info") {
        title = "INFO";
    } else if (type === "success") {
        title = "SUCCESS";
    }
    createToast(type, title, message);
}

/* ================= REGISTER EVENTS ================= */

Cef.registerEventCallback(
    "alert_show",
    "showAlert"
);

Cef.registerEventCallback(
    "notification_show",
    "showNotification"
);
