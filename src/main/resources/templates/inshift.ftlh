<#import "parts/common.ftlh" as c>
<@c.page>
<div>
    <@l.logout />
</div>
<a href="/">На главную</a>
<form>
    <p>
        <input type="date" name="calendar" value=${date}
               max="endYear" min="startYear">
        <input type="submit" value="Выбрать">
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </p>
</form>
<table class="table table-striped">
  <thead>
        <tr>
            <th scope="col">Сотрудник</th>
            <th scope="col">Время смены</th>
            <th scope="col">Тип смены</th>
       </tr>
   </thead>
   <tr scope="row">
        <td colspan="3"><b>Дневная смена</b></td>
   </tr>
<#list repos as repo>
    <tr scope="row">
        <td>${repo.name}</td>
        <td><i>${repo.description}</i></td>
        <td>${repo.shiftType}</td>
    </tr>
</#list>
    <tr scope="row">
        <td colspan="3"><b>В ночь:</b></td>
    </tr>
<#list nights as night>
    <tr scope="row">
        <td>${night.name}</td>
        <td><i>${night.description}</i></td>
        <td>${night.shiftType}</td>
    </tr>
<#else>
Not found shift
</#list>
</table>
</@c.page>
