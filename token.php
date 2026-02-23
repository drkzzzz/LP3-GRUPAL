<?php 
	if($_SERVER["REQUEST_METHOD"] == "POST"){
		

$curl = curl_init();

curl_setopt_array($curl, array(
  CURLOPT_URL => 'localhost:8080/restful/registros',
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => '',
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 0,
  CURLOPT_FOLLOWLOCATION => true,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => 'POST',
  CURLOPT_POSTFIELDS =>'{
    "nombres": "' . $_POST['nombres'] . '",
    "apellidos": "' . $_POST['apellidos'] . '",
    "email": "' . $_POST['email'] . '",
    "cliente_id": "",
    "llave_secreta": "",
    "access_token": ""
}',
  CURLOPT_HTTPHEADER => array(
    'Content-Type: application/json',
    'Cookie: JSESSIONID=F4B2CACB09D932DEFF58C097D3636FB1'
  ),
));

$response = curl_exec($curl);

curl_close($curl);
$data = json_decode($response, true);
	

$curl = curl_init();

curl_setopt_array($curl, array(
  CURLOPT_URL => 'localhost:8080/restful/token',
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => '',
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 0,
  CURLOPT_FOLLOWLOCATION => true,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => 'POST',
  CURLOPT_POSTFIELDS =>'{
     "cliente_id": "'.$data['cliente_id'] . '",
    "llave_secreta": "' . $data['email'] . $data['nombres'] . $data['apellidos'] .'"
}',
  CURLOPT_HTTPHEADER => array(
    'Content-Type: application/json',
    'Cookie: JSESSIONID=F4B2CACB09D932DEFF58C097D3636FB1'
  ),
));

$response = curl_exec($curl);

curl_close($curl);
$resultado = json_decode($response);

	}
		
?>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>DrinkGo - API de Acceso</title>
	<link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
	<style>
		* {
			margin: 0;
			padding: 0;
			box-sizing: border-box;
		}

		body {
			font-family: 'Poppins', sans-serif;
			min-height: 100vh;
			display: flex;
			align-items: center;
			justify-content: center;
			background: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)),
						url('https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80') center/cover no-repeat fixed;
			color: #fff;
			padding: 20px;
		}

		.container {
			max-width: 700px;
			width: 100%;
			text-align: center;
		}

		.logo {
			width: 200px;
			height: 200px;
			margin: 0 auto 30px;
			background: #fff;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 10px 40px rgba(0,0,0,0.5);
			padding: 10px;
			border: 6px solid rgba(255,255,255,0.95);
		}

		.logo img {
			width: 100%;
			height: 100%;
			object-fit: contain;
			border-radius: 50%;
		}

		h1 {
			font-family: 'Playfair Display', serif;
			font-size: 3.8rem;
			font-weight: 700;
			margin-bottom: 15px;
			text-shadow: 3px 3px 10px rgba(0,0,0,0.7);
			letter-spacing: 3px;
			background: linear-gradient(135deg, #ff9800, #ff5722);
			-webkit-background-clip: text;
			-webkit-text-fill-color: transparent;
			background-clip: text;
		}

		h2 {
			font-size: 1.4rem;
			margin-bottom: 10px;
			font-weight: 400;
			color: #ecf0f1;
			letter-spacing: 2px;
		}

		.subtitle {
			font-size: 1.1rem;
			margin-bottom: 25px;
			font-weight: 300;
			letter-spacing: 3px;
			text-transform: uppercase;
			color: #bdc3c7;
		}

		.description {
			font-size: 0.95rem;
			line-height: 1.8;
			margin-bottom: 30px;
			padding: 0 20px;
			color: #ecf0f1;
			background: rgba(0,0,0,0.3);
			padding: 20px;
			border-radius: 10px;
			border-left: 4px solid #ff9800;
		}

		.token-container {
			background: linear-gradient(135deg, #27ae60, #229954);
			border-radius: 15px;
			padding: 30px;
			margin-bottom: 30px;
			border: 3px solid #2ecc71;
			animation: slideDown 0.6s ease;
			box-shadow: 0 10px 40px rgba(39, 174, 96, 0.4);
		}

		.token-header {
			font-size: 1.3rem;
			font-weight: 600;
			margin-bottom: 20px;
			color: #fff;
			text-align: center;
			text-transform: uppercase;
			letter-spacing: 2px;
		}

		.token-box {
			background: rgba(255, 255, 255, 0.98);
			color: #2c3e50;
			padding: 20px;
			border-radius: 10px;
			word-break: break-all;
			font-family: 'Courier New', Courier, monospace;
			font-size: 0.88rem;
			margin-bottom: 20px;
			max-height: 180px;
			overflow-y: auto;
			cursor: pointer;
			user-select: all;
			border: 3px solid #27ae60;
			box-shadow: inset 0 2px 8px rgba(0,0,0,0.1);
		}

		.token-box:hover {
			background: #fff;
			box-shadow: inset 0 2px 8px rgba(0,0,0,0.15);
		}

		.btn-copy {
			width: 100%;
			padding: 14px;
			border: none;
			border-radius: 10px;
			background: #fff;
			color: #27ae60;
			font-size: 1rem;
			font-weight: 600;
			cursor: pointer;
			transition: all 0.3s;
			margin-bottom: 15px;
			text-transform: uppercase;
			letter-spacing: 1px;
		}

		.btn-copy:hover {
			background: #ecf0f1;
			transform: translateY(-3px);
			box-shadow: 0 5px 20px rgba(255,255,255,0.3);
		}

		.token-info {
			background: rgba(255, 255, 255, 0.15);
			padding: 15px;
			border-radius: 8px;
			font-size: 0.88rem;
			text-align: center;
			color: #fff;
		}

		.form-container {
			background: rgba(255, 255, 255, 0.12);
			backdrop-filter: blur(15px);
			border-radius: 20px;
			padding: 40px;
			box-shadow: 0 10px 40px rgba(0,0,0,0.4);
			border: 1px solid rgba(255,255,255,0.2);
		}

		.form-title {
			font-size: 1.5rem;
			font-weight: 600;
			margin-bottom: 25px;
			color: #fff;
			letter-spacing: 1px;
		}

		.form-group {
			margin-bottom: 20px;
			text-align: left;
		}

		.form-group input {
			width: 100%;
			padding: 16px 20px;
			border: none;
			border-radius: 10px;
			font-size: 1rem;
			background: rgba(255, 255, 255, 0.95);
			color: #2c3e50;
			font-family: 'Poppins', sans-serif;
			transition: all 0.3s;
			border: 2px solid transparent;
		}

		.form-group input:focus {
			outline: none;
			background: #fff;
			border: 2px solid #ff9800;
			box-shadow: 0 0 0 4px rgba(255, 152, 0, 0.2);
		}

		.form-group input::placeholder {
			color: #95a5a6;
		}

		.btn-register {
			width: 100%;
			padding: 16px;
			border: none;
			border-radius: 10px;
			background: linear-gradient(135deg, #ff9800, #ff5722);
			color: #fff;
			font-size: 1.1rem;
			font-weight: 600;
			text-transform: uppercase;
			letter-spacing: 2px;
			cursor: pointer;
			transition: all 0.3s;
			margin-top: 10px;
			box-shadow: 0 6px 25px rgba(255, 87, 34, 0.5);
		}

		.btn-register:hover {
			background: linear-gradient(135deg, #ff5722, #e64a19);
			transform: translateY(-3px);
			box-shadow: 0 8px 30px rgba(255, 87, 34, 0.7);
		}

		.btn-register:active {
			transform: translateY(0);
		}

		.back-link {
			color: #ff9800;
			text-decoration: none;
			font-size: 1rem;
			padding: 12px 25px;
			border: 2px solid #ff9800;
			border-radius: 10px;
			display: inline-block;
			transition: all 0.3s;
			background: rgba(255, 152, 0, 0.1);
			margin-top: 20px;
		}

		.back-link:hover {
			background: #ff9800;
			color: #fff;
			transform: translateY(-3px);
			box-shadow: 0 5px 20px rgba(255, 152, 0, 0.5);
			text-decoration: none;
		}

		@keyframes slideDown {
			from {
				opacity: 0;
				transform: translateY(-20px);
			}
			to {
				opacity: 1;
				transform: translateY(0);
			}
		}

		@media (max-width: 768px) {
			h1 {
				font-size: 2.8rem;
			}
			
			h2 {
				font-size: 1.1rem;
			}

			.logo {
				width: 160px;
				height: 160px;
			}

			.form-container {
				padding: 30px 25px;
			}

			.description {
				font-size: 0.88rem;
			}
		}
	</style>
</head>
<body>
	<div class="container">
		<!-- Logo -->
		<div class="logo">
			<img src="drinkgo-logo.png" alt="DrinkGo Logo" onerror="this.style.display='none'; this.parentElement.innerHTML='<div style=\'font-family: Playfair Display, serif; font-size: 38px; font-weight: 700; color: #ff9800; line-height: 1.2;\'><div style=\'font-size: 50px;\'>DRINK</div><div style=\'font-size: 36px; color: #2c3e50; letter-spacing: 3px;\'>GO</div></div>'">
		</div>

		<!-- Título principal -->
		<h1>DrinkGo</h1>
		
		<!-- Subtítulos -->
		<h2>Desarrolla aplicaciones con nosotros</h2>
		<p class="subtitle">Accede a nuestros servicios</p>

		<!-- Descripción -->
		<div class="description">
			Bienvenidos, la presente plataforma contiene la API desarrollada para el modelo de negocio. La API permite el acceso directo al trabajo con la información de nuestra base de datos desde tu aplicación. Usa una interfaz RESTful y retorna los datos en formato JSON. La información invocadas a través de la API proveen un acceso estándar online a datos contenidos en páginas HTML y otros archivos similares disponibles en Internet. Registre sus datos y consiga la autorización para trabajar en nuestra plataforma, ya que requiere de datos de autorización, la puedes solicitar a través del siguiente formulario:
		</div>

		<!-- Token generado -->
		<?php if($_SERVER["REQUEST_METHOD"] == "POST") : ?>
			<div class="token-container">
				<div class="token-header">
					✅ Token de Acceso Generado
				</div>

				<div class="token-box" id="tokenText" title="Haz clic para seleccionar">
					<?php echo $resultado->token ?>
				</div>

				<button class="btn-copy" onclick="copiarToken()">
					📋 Copiar Token
				</button>

				<div class="token-info">
					<small>
						Usa este token en Postman:<br>
						<strong>Authorization → Bearer Token</strong>
					</small>
				</div>
			</div>
			
			<div style="text-align: center;">
				<a href="token.php" class="back-link">
					← Registrar Otro Usuario
				</a>
			</div>
		<?php else : ?>
			<!-- Formulario de solicitud -->
			<div class="form-container">
				<h3 class="form-title">Solicitud de Acceso a Datos</h3>
				<form method="POST" action="">
					<div class="form-group">
						<input type="text" 
							   name="nombres" 
							   placeholder="Nombres" 
							   required>
					</div>

					<div class="form-group">
						<input type="text" 
							   name="apellidos" 
							   placeholder="Apellidos" 
							   required>
					</div>

					<div class="form-group">
						<input type="email" 
							   name="email" 
							   placeholder="E-mail" 
							   required>
					</div>

					<button type="submit" class="btn-register">Registrar</button>
				</form>
			</div>
		<?php endif ?>
	</div>

	<script>
		function copiarToken() {
			var tokenText = document.getElementById('tokenText').innerText.trim();
			
			navigator.clipboard.writeText(tokenText).then(function() {
				alert('✅ Token copiado exitosamente.\n\nAhora úsalo en Postman:\n• Authorization → Type: Bearer Token\n• Pega el token y accede a todos los endpoints');
			}).catch(function() {
				// Fallback para navegadores antiguos
				var textArea = document.createElement('textarea');
				textArea.value = tokenText;
				document.body.appendChild(textArea);
				textArea.select();
				document.execCommand('copy');
				document.body.removeChild(textArea);
				alert('✅ Token copiado al portapapeles.');
			});
		}
	</script>
</body>
</html>