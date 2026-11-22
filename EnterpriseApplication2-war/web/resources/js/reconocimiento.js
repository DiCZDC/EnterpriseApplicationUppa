/*
 * Úppa — captura de foto para vistas/reconocimiento/index.xhtml
 * Port de la lógica Alpine.js del componente Livewire file.upload:
 *   - vista previa del archivo elegido
 *   - cámara (getUserMedia), cambio frontal/trasera y captura a JPEG
 *   - la captura se inyecta en el <input type=file> (DataTransfer) y se envía con el formulario JSF
 *   - indicador "Identificando especie..." mientras se espera la respuesta
 * Nota: la cámara solo funciona en https:// o en http://localhost (requisito de los navegadores).
 */
(function () {
    'use strict';

    var form = document.getElementById('formAnalisis');
    if (!form) {
        return; // estado "resultado": no hay formulario de captura
    }

    var input = form.querySelector('input[type="file"]');
    var btnAnalizar = form.querySelector('input[type="submit"], button[type="submit"]');
    var video = document.getElementById('camVideo');
    var canvas = document.getElementById('camCanvas');
    var preview = document.getElementById('vistaPrevia');
    var btnCamara = document.getElementById('btnCamara');
    var btnGirar = document.getElementById('btnGirar');
    var btnDisparo = document.getElementById('btnDisparo');
    var zonaArchivo = document.getElementById('zonaArchivo');
    var ayuda = document.getElementById('ayudaCamara');
    var analizando = document.getElementById('analizando');
    var captura = document.getElementById('captura');
    var barra = document.getElementById('barraProgreso');

    var stream = null;
    var facingMode = 'environment';
    var urlPrevia = null;

    function mostrar(el, si) { if (el) { el.classList.toggle('oculto', !si); } }

    function actualizarBoton() {
        if (btnAnalizar) {
            btnAnalizar.disabled = !(input && input.files && input.files.length > 0);
        }
    }

    function mostrarPrevia(file) {
        if (urlPrevia) { URL.revokeObjectURL(urlPrevia); }
        urlPrevia = URL.createObjectURL(file);
        preview.src = urlPrevia;
        mostrar(preview, true);
    }

    function modoCamara(activo) {
        mostrar(video, activo);
        mostrar(btnGirar, activo);
        mostrar(btnDisparo, activo);
        mostrar(ayuda, activo);
        mostrar(zonaArchivo, !activo);
        mostrar(preview, !activo && !!preview.getAttribute('src'));
        btnCamara.classList.toggle('activo', activo);
    }

    function detenerCamara() {
        if (stream) {
            stream.getTracks().forEach(function (t) { t.stop(); });
            stream = null;
        }
        modoCamara(false);
    }

    function iniciarCamara() {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            alert('Tu navegador no permite usar la cámara aquí. Usa https:// o localhost, o sube un archivo.');
            return;
        }
        navigator.mediaDevices.getUserMedia({ video: { facingMode: facingMode }, audio: false })
            .then(function (s) {
                stream = s;
                video.srcObject = s;
                modoCamara(true);
            })
            .catch(function (e) {
                alert('No se pudo acceder a la cámara: ' + e.message);
                detenerCamara();
            });
    }

    function capturar() {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        canvas.getContext('2d').drawImage(video, 0, 0);
        canvas.toBlob(function (blob) {
            var file = new File([blob], 'captura.jpg', { type: 'image/jpeg' });
            var dt = new DataTransfer();
            dt.items.add(file);
            input.files = dt.files;
            input.dispatchEvent(new Event('change'));
            detenerCamara();
        }, 'image/jpeg', 0.92);
    }

    btnCamara.addEventListener('click', function () {
        if (stream) { detenerCamara(); } else { iniciarCamara(); }
    });
    btnGirar.addEventListener('click', function () {
        facingMode = facingMode === 'environment' ? 'user' : 'environment';
        if (stream) { stream.getTracks().forEach(function (t) { t.stop(); }); stream = null; }
        iniciarCamara();
    });
    btnDisparo.addEventListener('click', capturar);
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && stream) { detenerCamara(); }
    });

    input.addEventListener('change', function () {
        if (input.files && input.files[0]) {
            if (input.files[0].size > 10 * 1024 * 1024) {
                alert('La imagen no debe superar los 10 MB.');
                input.value = '';
            } else {
                mostrarPrevia(input.files[0]);
            }
        }
        actualizarBoton();
    });

    form.addEventListener('submit', function () {
        detenerCamara();
        mostrar(captura, false);
        mostrar(analizando, true);
        var p = 0;
        setInterval(function () {
            p = Math.min(95, p + Math.random() * 10);
            barra.style.width = p + '%';
        }, 300);
    });

    actualizarBoton();
})();
