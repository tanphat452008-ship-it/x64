let selectedItem = null;
let isEquipSlotSelected = false;

// ===============================
// HIỂN THỊ INVENTORY
// ===============================
function renderInventory(eventData)
{
    try
    {
        console.log("CEF DATA:", eventData);

        const data = JSON.parse(eventData);

        const items = JSON.parse(data[1] || "[]");
        const equips = JSON.parse(data[2] || "[]");
        document.getElementById("inventory-container").style.display = "flex";
        document.getElementById("detail-placeholder").classList.remove("hidden");
        document.getElementById("detail-content").classList.add("hidden");

        selectedItem = null;
        isEquipSlotSelected = false;
        const grid = document.getElementById("inv-grid");

        grid.innerHTML = "";

        document.getElementById("slot-text").innerText =
            `${items.length} / 56 SLOT`;

        for (let i = 0; i < 56; i++)
        {
            const slot = document.createElement("div");

            slot.className = "inv-slot";

            if (i < items.length)
            {
                slot.innerHTML = `
                    <div class="item-qty">x${items[i].amount}</div>

                    <img 
                        src="${items[i].icon}"
                        style="
                            width:75%;
                            height:75%;
                            margin:12.5%;
                            object-fit:contain;
                        "
                    >
                `;

                slot.onclick = () =>
                {
                    removeActiveStates();

                    slot.classList.add("active-slot");

                    showItemDetails(items[i], false);
                };
            }

            grid.appendChild(slot);
        }
        for (let slotId = 0; slotId < 6; slotId++)
        {
            const eSlot = document.getElementById(`equip-${slotId}`);

            const eqItem = equips.find(e => e.type == slotId);

            if (eqItem)
            {
                eSlot.innerHTML = `
                    <img 
                        src="${eqItem.icon}"
                        style="
                            width:80%;
                            height:80%;
                            margin:10%;
                            object-fit:contain;
                        "
                    >
                `;

                eSlot.classList.remove("empty");

                eSlot.onclick = () =>
                {
                    removeActiveStates();

                    eSlot.classList.add("active-slot");

                    showItemDetails(eqItem, true);
                };
            }
            else
            {
                eSlot.innerHTML = "";

                eSlot.classList.add("empty");

                eSlot.onclick = () =>
                {
                    clickEquipSlot(slotId);
                };
            }
        }
    }
    catch(err)
    {
        console.log("Inventory Error:", err);
    }
}
function showItemDetails(item, isEquipped)
{
    selectedItem = item;

    isEquipSlotSelected = isEquipped;

    document.getElementById("detail-placeholder")
        .classList.add("hidden");

    document.getElementById("detail-content")
        .classList.remove("hidden");

    document.getElementById("detail-name
