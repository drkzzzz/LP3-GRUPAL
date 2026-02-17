<?php
	// ============================================
	// token.php - Obtención de JWT desde DrinkGo Backend
	// Backend: Spring Boot | Puerto: 8081
	// Endpoint: POST http://localhost:8081/restful/token
	// ============================================

	$token = null;
	$error = null;

	if ($_SERVER["REQUEST_METHOD"] == "POST") {

		// --- Paso 1: Preparar los datos del formulario ---
		$clienteId    = trim($_POST['clienteId'] ?? '');
		$llaveSecreta = trim($_POST['llaveSecreta'] ?? '');

		// Validación básica en servidor
		if (empty($clienteId) || empty($llaveSecreta)) {
			$error = "Todos los campos son obligatorios.";
		} else {

			// --- Paso 2: Construir el body JSON para el backend ---
			$body = json_encode([
				"clienteId"    => $clienteId,
				"llaveSecreta" => $llaveSecreta
			]);

			// --- Paso 3: Enviar solicitud POST con cURL al backend Spring Boot ---
			$curl = curl_init();

			curl_setopt_array($curl, [
				CURLOPT_URL            => 'http://localhost:8081/restful/token',
				CURLOPT_RETURNTRANSFER => true,
				CURLOPT_ENCODING       => '',
				CURLOPT_MAXREDIRS      => 10,
				CURLOPT_TIMEOUT        => 30,
				CURLOPT_FOLLOWLOCATION => true,
				CURLOPT_HTTP_VERSION   => CURL_HTTP_VERSION_1_1,
				CURLOPT_CUSTOMREQUEST  => 'POST',
				CURLOPT_POSTFIELDS     => $body,
				CURLOPT_HTTPHEADER     => [
					'Content-Type: application/json',
					'Accept: application/json'
				],
			]);

			$response  = curl_exec($curl);
			$httpCode  = curl_getinfo($curl, CURLINFO_HTTP_CODE);
			$curlError = curl_error($curl);
			curl_close($curl);

			// --- Paso 4: Procesar la respuesta del backend ---
			if ($curlError) {
				// Error de conexión (backend apagado, puerto incorrecto, etc.)
				$error = "Error de conexión con el backend: " . htmlspecialchars($curlError);
			} else {
				$resultado = json_decode($response);

				if ($httpCode === 200 && $resultado && isset($resultado->accessToken)) {
					// Token JWT obtenido exitosamente
					$token = $resultado->accessToken;
				} else {
					// El backend respondió pero con error (credenciales inválidas, etc.)
					$mensajeError = isset($resultado->message)
						? $resultado->message
						: (isset($resultado->error) ? $resultado->error : $response);
					$error = "Error del backend (HTTP $httpCode): " . htmlspecialchars($mensajeError);
				}
			}
		}
	}
?>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>DrinkGo - Obtener Token JWT</title>
	<!-- Bootstrap 4 CDN -->
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css"
		  integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk"
		  crossorigin="anonymous">
	<style>
		.token-box {
			word-break: break-all;
			font-family: 'Courier New', Courier, monospace;
			font-size: 0.95rem;
			cursor: pointer;
			user-select: all;
			padding: 15px;
		}
	</style>
</head>
<body class="bg-light">
<div class="container py-5">

	<!-- Encabezado -->
	<div class="text-center mb-4">
		<h1 class="display-4">DrinkGo API</h1>
		<h2 class="text-muted">Obtención de Token JWT</h2>
		<p class="lead">
			Ingrese sus credenciales para obtener el token de acceso.<br>
			El token generado se puede usar como <strong>Bearer Token</strong> en Postman.
		</p>
	</div>

	<!-- Mensaje de error -->
	<?php if ($error) : ?>
		<div class="row justify-content-center mb-3">
			<div class="col-xl-8">
				<div class="alert alert-danger alert-dismissible fade show" role="alert">
					<strong>Error:</strong> <?php echo $error; ?>
					<button type="button" class="close" data-dismiss="alert" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
			</div>
		</div>
	<?php endif; ?>

	<!-- Token JWT obtenido -->
	<?php if ($token) : ?>
		<div class="row justify-content-center mb-4">
			<div class="col-xl-8">
				<div class="card border-success">
					<div class="card-header bg-dark text-white font-weight-bold">
						Token JWT obtenido exitosamente
					</div>
					<div class="card-body bg-success text-white token-box" id="tokenText"
						 title="Haz clic para seleccionar todo el token">
						<?php echo htmlspecialchars($token); ?>
					</div>
					<div class="card-footer text-center">
						<button class="btn btn-outline-dark" onclick="copiarToken()">
							Copiar Token al Portapapeles
						</button>
						<small class="d-block mt-2 text-muted">
							Usa este token en Postman: <code>Authorization - Bearer Token</code>
						</small>
					</div>
				</div>
			</div>
		</div>
	<?php endif; ?>

	<!-- Formulario de solicitud de token -->
	<div class="row justify-content-center">
		<div class="col-xl-8">
			<div class="card shadow-sm">
				<div class="card-header bg-primary text-white">
					<h4 class="mb-0">Solicitar Token de Acceso</h4>
				</div>
				<div class="card-body">
					<form method="POST" action="">
						<!-- Campo: Cliente ID (UUID) -->
						<div class="form-group">
							<label for="clienteId"><strong>Cliente ID (UUID)</strong></label>
							<input type="text"
								   name="clienteId"
								   id="clienteId"
								   class="form-control"
								   placeholder="Ej: 550e8400-e29b-41d4-a716-446655440000"
								   value="<?php echo htmlspecialchars($_POST['clienteId'] ?? ''); ?>"
								   required>
							<small class="form-text text-muted">
								Identificador único del cliente registrado en el sistema.
							</small>
						</div>

						<!-- Campo: Llave Secreta (Password) -->
						<div class="form-group">
							<label for="llaveSecreta"><strong>Llave Secreta (Password)</strong></label>
							<input type="password"
								   name="llaveSecreta"
								   id="llaveSecreta"
								   class="form-control"
								   placeholder="Ingrese su llave secreta"
								   required>
							<small class="form-text text-muted">
								Contraseña o llave secreta asociada al cliente.
							</small>
						</div>

						<!-- Botón de envío -->
						<button type="submit" class="btn btn-success btn-lg btn-block">
							Obtener Token JWT
						</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Información adicional -->
	<div class="row justify-content-center mt-4">
		<div class="col-xl-8">
			<div class="card bg-light border">
				<div class="card-body">
					<h5>¿Cómo usar el token en Postman?</h5>
					<ol>
						<li>Copie el token JWT generado arriba.</li>
						<li>Abra Postman y seleccione la pestaña <strong>Authorization</strong>.</li>
						<li>En "Type" seleccione <strong>Bearer Token</strong>.</li>
						<li>Pegue el token en el campo "Token".</li>
						<li>Envíe su petición al endpoint deseado.</li>
					</ol>
					<hr>
					<p class="mb-0 text-muted">
						<strong>Backend:</strong> Spring Boot (Puerto 8081) |
						<strong>Endpoint:</strong> POST /restful/token |
						<strong>Formato:</strong> JSON
					</p>
				</div>
			</div>
		</div>
	</div>

</div>

<!-- jQuery, Popper.js, Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"
		integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
		crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
		integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
		crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"
		integrity="sha384-OgVRvuATP1z7JjHLkuOU7Xw704+h835Lr+6QL9UvYjZE3Ipu6Tp75j7Bh/kR0JKI"
		crossorigin="anonymous"></script>

<script>
	// Función para copiar el token al portapapeles
	function copiarToken() {
		var tokenText = document.getElementById('tokenText').innerText.trim();
		navigator.clipboard.writeText(tokenText).then(function() {
			alert('Token copiado al portapapeles.\nPéguelo en Postman como Bearer Token.');
		}).catch(function() {
			// Fallback para navegadores antiguos
			var textArea = document.createElement('textarea');
			textArea.value = tokenText;
			document.body.appendChild(textArea);
			textArea.select();
			document.execCommand('copy');
			document.body.removeChild(textArea);
			alert('Token copiado al portapapeles.');
		});
	}
</script>
</body>
</html>