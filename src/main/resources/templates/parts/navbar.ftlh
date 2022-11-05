<#include "security.ftlh">
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
      <#if isAdmin>
      <li class="nav-item">
        <a class="nav-link" href="/user">Пользователи</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="/upload">Загрузить</a>
      </li>
      </#if>
    </ul>
    <ul class="nav navbar-nav ml-auto">
      <li class="nav-item">
        <a class="nav-link" href="/login"><span class="fas fa-sign-in-alt"></span> Login</a>
      </li>
    </ul>
  </div>
</nav>