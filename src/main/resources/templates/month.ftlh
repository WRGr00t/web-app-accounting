<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap demo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="css\bootstrap-datepicker3.min.css">
  </head>
  <body>
    <h3>Выберите месяц для анализа</h3>
    <div class="container">
    	<div class="row">
    		<div class="col-12" id="sandbox-container">
    			<div class="input-group date">
  					<input type="text" class="form-control"><span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
					</div>
    		</div>
    	</div>
   	</div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-OERcA2EqjJCMA+/3y+gxIOqMEjwtxJY7qPCqsdltbNJuaOe923+mo//f6V8Qbsw3" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="js\bootstrap-datepicker.ru.min.js"></script>
    <script src="js\bootstrap-datepicker.min.js"></script>
    <script>
    	$('#sandbox-container .input-group.date').datepicker({
		    format: "mm/yyyy",
		    startView: 1,
		    minViewMode: 1,
		    autoclose: true
				});
    </script>
  </body>
</html>