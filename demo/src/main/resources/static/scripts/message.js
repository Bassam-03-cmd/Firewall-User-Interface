// function closeMessage()
// {
//     messages = document.getElementsByClassName("message");
//     for (const message of messages) {
//         message.style.display = "none";
//     }
// }

function closeMessage() {
    const msg = document.querySelector(".message");
    if (msg) msg.style.display = "none";
}