<#include "security.ftlh">
<#import "login.ftlh" as l>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <a class="navbar-brand" href="/">
  <#if subpage??>
        <img src="../static/img/icon.svg" width="30" height="30" class="d-inline-block align-top" alt="">На смене</a>
  <#else>
        <img src="static/img/icon.svg" width="30" height="30" class="d-inline-block align-top" alt="">На смене</a>
  </#if>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
        <#if user??>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  Сотруднику
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                  <a class="dropdown-item" href="/inshift">Список смены</a>
                  <a class="dropdown-item" href="/forpersonal">Ближайшие смены</a>
                  <a class="dropdown-item" href="/calendar">Календарь на год</a>
                  <a class="dropdown-item" href="/news">История изменений</a>
                  <#-- <a class="dropdown-item" href="/monthcalendar">Календарь на месяц</a> -->
                </div>
            </li>
            <li class="nav-item dropdown">
                  <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          Руководителю
                  </a>
                  <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                       <a class="dropdown-item" href="/inmonth">Анализ месяца</a>
                       <a class="dropdown-item" href="/distrib">Распределение</a>
                  </div>
            </li>
         <li class="nav-item">
            <a class="nav-link" href="/user/profile">Профиль</a>
         </li>
      </#if>
      <#if isAdmin>
        <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                   Админу
              </a>
              <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                  <a class="dropdown-item" href="/user">Пользователи</a>
                  <a class="dropdown-item" href="/subscribe">Рассылки</a>
                  <a class="dropdown-item" href="/upload">Загрузить</a>
              </div>
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