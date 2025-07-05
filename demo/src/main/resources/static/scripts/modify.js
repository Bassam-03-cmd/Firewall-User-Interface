document.addEventListener("DOMContentLoaded", () => {
  // Define each toggle button and its corresponding table
  const toggleButtons = [
    { buttonId: "modify-users", tableId: "users-table" },
    { buttonId: "modify-rules", tableId: "rules-table" }
  ];

  toggleButtons.forEach(({ buttonId, tableId }) => {
    const btn = document.getElementById(buttonId);
    if (!btn) return;  // Skip if that page doesn’t have this button

    let editing = false;
    btn.addEventListener("click", () => {
      editing = !editing;

      // Toggle 'disabled' on all inputs & selects inside the target table
      document
        .querySelectorAll(`#${tableId} input, #${tableId} select`)
        .forEach(el => {
          el.disabled = !editing;
          el.classList.toggle("editable", editing);
        });

      // Switch button text between 'Modify' and 'Cancel'
      btn.textContent = editing ? "Cancel" : "Modify";
    });
  });
});



