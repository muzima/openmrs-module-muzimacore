let alertContainer;
let btn;
let alertIcon;
let alertMessage;
let alert;

function alertFunc(type, message) {
    getElements();
    if (type === 0) {
        alertIcon.classList.add('fa-times');
        alertIcon.classList.add('text-danger');
        alert.style.borderTop = "50px solid #0b9253";
    }else if (type === 1) {
        alertIcon.classList.add('fa-check');
        alertIcon.classList.add('text-success');
        alert.style.borderTop = "50px solid #262626";
    }
    alertMessage.innerHTML = message;
    alertContainer.classList.remove('hide');
    alert.classList.add("show-animation");
    
    setTimeout(() => {
        alertContainer.classList.add('hide-animation');
    }, 2000);

    setTimeout(() => {
        alertContainer.classList.add('hide');
        alert.classList.remove("show-animation");
        alertContainer.classList.remove('hide-animation');
        alertContainer.style.opacity = '1';
        alertIcon.className = '';
        alertIcon.classList.add('fas');
    }, 3000);
}

function getElements() {
    alertContainer = document.getElementById("alertContainer");
    btn = document.getElementById("btn");
    alertIcon = document.getElementById("alertIcon");
    alertMessage = document.getElementById("alertMessage");
    alert = document.getElementById('alert');
}
