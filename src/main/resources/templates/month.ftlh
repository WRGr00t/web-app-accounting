<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

            <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <title>InShiftApp</title>
    <link rel="icon" href="static/img/favicon.ico" type="image/x-icon">
    <!-- <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous"> -->
    <link rel="stylesheet" type="text/css" href="css\bootstrap-datepicker3.min.css">
  </head>
  <body>
  <#include "parts/security.ftlh">
  <#import "parts/login.ftlh" as l>

  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">
    <img src="static/img/icon.svg" width="30" height="30" class="d-inline-block align-top" alt="">На смене</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <a class="nav-link" href="/inshift">Список смены</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/inmonth">Анализ месяца</a>
        </li>
        <#if user??>
           <li class="nav-item">
              <a class="nav-link" href="/user/profile">Профиль</a>
           </li>
        </#if>
        <#if isAdmin>
        <li class="nav-item">
          <a class="nav-link" href="/user">Пользователи</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/upload">Загрузить</a>
        </li>
        </#if>
      </ul>
      <div class="navbar-text mr-3">
          ${name}
      </div>
      <#if !known>
      <form action="/login" method="post">
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Вoйти</button>
      </form>
      </#if>
      <#if known>
          <@l.logout />
      </#if>
    </div>
  </nav>
  <div class="container mt-5">
    <h3>Выберите месяц для анализа</h3>
    <div class="container">
    	<div class="row">
    		<div class="col-12" id="sandbox-container">
    			<div class="input-group date">
  					<input type="text" class="form-control" name="month" value=${date}>
  					<span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
  					<input type="hidden" name="_csrf" value="${_csrf.token}"/>
				</div>
    		</div>
    	</div>
   	</div>
   	</div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-OERcA2EqjJCMA+/3y+gxIOqMEjwtxJY7qPCqsdltbNJuaOe923+mo//f6V8Qbsw3" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.3/dist/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/i18n/datepicker-ru.js"></script>

  </body>
</html>