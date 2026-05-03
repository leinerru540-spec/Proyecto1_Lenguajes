// Controla el login desde la vista 
const loginForm = document.getElementById('loginForm');
const submitButton = document.getElementById('submitButton');
const tokenResult = document.getElementById('tokenResult');
const roleResult = document.getElementById('roleResult');
const statusMessage = document.getElementById('statusMessage');

function resolveRedirectByRole(role) {
    if (role === 'ADMINISTRADOR') {
        return '/admin';
    }

    if (role === 'CLIENTE') {
        return '/user';
    }

    return '/';
}

function showStatus(message, type) {
    statusMessage.textContent = message;
    statusMessage.className = `alert alert-${type} status-box d-block`;
}

if (loginForm && submitButton && tokenResult && roleResult && statusMessage) {
    loginForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        submitButton.disabled = true;
        submitButton.textContent = 'Validando acceso...';
        statusMessage.className = 'alert status-box';

        const payload = {
            email: document.getElementById('email').value.trim(),
            password: document.getElementById('password').value
        };

        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error('Credenciales invalidas o acceso no autorizado.');
            }

            const data = await response.json();
            const token = data.token || '';
            const rol = data.rol || '';
            tokenResult.value = token;
            roleResult.value = rol;

            const redirectUrl = resolveRedirectByRole(rol);
            showStatus(`Inicio de sesion exitoso. Redirigiendo a ${redirectUrl}...`, 'success');

            setTimeout(function () {
                window.location.href = redirectUrl;
            }, 900);
        } catch (error) {
            tokenResult.value = '';
            roleResult.value = '';
            showStatus(error.message || 'No fue posible iniciar sesion en este momento.', 'danger');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Entrar al sistema';
        }
    });
}
