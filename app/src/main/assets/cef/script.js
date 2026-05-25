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

    // Dọn sạch hộp chứa để xóa bỏ hoàn toàn tin nhắn cũ trước khi hiện tin mới
    container.innerHTML = ""; 

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

/* ================= INVENTORY (ĐÃ SỬA ĐỔI & THÊM LOGIC) ================= */

// Biến toàn cục để lưu thông tin vật phẩm đang được người chơi ấn chọn
let selectedItem = null; 

// Sự kiện bấm nút đóng túi đồ lớn
document.getElementById('close-inv-btn').onclick = () => {
    document.getElementById('inventory-container').classList.add('hidden');
    
    // Tự động đóng luôn các menu con nếu đang mở
    closeActionMenu();
    closeInfoMenu();

    // Gửi tín hiệu về cho Pawn biết người chơi đã đóng túi đồ
    Cef.sendEvent("inventory_action", JSON.stringify(["close", 0]));
};

function renderInventory(eventData) {
    const data = JSON.parse(eventData);
    const action = data[0]; // "show" hoặc "hide"
    
    const container = document.getElementById('inventory-container');

    if (action === "hide") {
        container.classList.add('hidden');
        closeActionMenu();
        closeInfoMenu();
        return;
    }

    // Mở túi đồ
    container.classList.remove('hidden');
    
    // Parse dữ liệu vật phẩm Pawn gửi qua
    const items = JSON.parse(data[1]); 
    const grid = document.getElementById('inv-grid');
    grid.innerHTML = ""; // Xóa dữ liệu cũ

    const TOTAL_SLOTS = 16; // Số ô đồ mặc định là 16 (4x4)

    for (let i = 0; i < TOTAL_SLOTS; i++) {
        const slot = document.createElement("div");
        slot.classList.add("inv-slot");

        if (i < items.length) {
            // Có vật phẩm ở slot này
            slot.innerHTML = `
                <div class="item-name">${items[i].name}</div>
                <div class="item-qty">x${items[i].amount}</div>
            `;
            
            // ĐÃ SỬA: Khi click vào vật phẩm -> Hiện bảng Menu tương tác chứ không dùng luôn
            slot.onclick = () => {
                openActionMenu(items[i]);
            };
        } else {
            // Slot trống
            slot.classList.add("empty");
        }

        grid.appendChild(slot);
    }
}

// ---- CÁC HÀM XỬ LÝ ĐÓNG / MỞ BẢNG TƯƠNG TÁC ----

function openActionMenu(item) {
    selectedItem = item; // Gán vật phẩm được chọn vào biến tạm
    document.getElementById('action-item-name').innerText = item.name;
    document.getElementById('item-action-modal').classList.remove('hidden');
}

function closeActionMenu() {
    document.getElementById('item-action-modal').classList.add('hidden');
}

function closeInfoMenu() {
    document.getElementById('item-info-modal').classList.add('hidden');
}

// ---- SỰ KIỆN KHI BẤM CÁC NÚT TRONG MENU TƯƠNG TÁC ----

// 1. Xử lý khi bấm nút "SỬ DỤNG"
document.getElementById('btn-use-item').onclick = () => {
    if (!selectedItem) return;
    
    // Gửi Action "use" kèm ID vật phẩm về cho Pawn xử lý
    Cef.sendEvent("inventory_action", JSON.stringify(["use", selectedItem.id]));
    
    closeActionMenu(); // Dùng xong tự ẩn menu tương tác đi
};

// 2. Xử lý khi bấm nút "THÔNG TIN"
document.getElementById('btn-info-item').onclick = () => {
    if (!selectedItem) return;
    
    document.getElementById('info-item-title').innerText = selectedItem.name;
    
    // Nếu trong chuỗi JSON từ Pawn gửi qua có trường .desc thì hiện, không thì hiện mặc định
    document.getElementById('info-item-desc').innerText = selectedItem.desc || "Vật phẩm này không có mô tả chi tiết.";
    
    // Mở bảng thông tin đè lên
    document.getElementById('item-info-modal').classList.remove('hidden');
};

// 3. Xử lý khi bấm nút "VỨT BỎ"
document.getElementById('btn-drop-item').onclick = () => {
    if (!selectedItem) return;
    
    // Gửi Action "drop" kèm ID vật phẩm về cho Pawn xử lý trừ đồ / tạo vật thể dưới đất
    Cef.sendEvent("inventory_action", JSON.stringify(["drop", selectedItem.id]));
    
    closeActionMenu(); // Vứt xong tự ẩn menu tương tác đi
};

// Đăng ký sự kiện cho các nút quay lại/đóng của menu con
document.getElementById('btn-close-action').onclick = closeActionMenu;
document.getElementById('btn-close-info').onclick = closeInfoMenu;

// Đăng ký event để Pawn gọi
Cef.registerEventCallback("inventory_show", "renderInventory");

/* ================= REGISTER EVENTS ================= */

Cef.registerEventCallback(
    "alert_show",
    "showAlert"
);

Cef.registerEventCallback(
    "notification_show",
    "showNotification"
);
