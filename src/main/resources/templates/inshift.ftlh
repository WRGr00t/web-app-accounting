<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
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
<table border="1">
<caption>${date} на смене</caption>
    <tr>
        <th>Сотрудник</th>
        <th>Время смены</th>
        <th>Тип смены</th>
   </tr>
   <tr>
        <td colspan="3">Дневная смена</td>
   </tr>
<#list repos as repo>
    <tr>
        <td><b>${repo.name}</b></td>
        <td><i>${repo.description}</i></td>
        <td>${repo.shiftType}</td>
    </tr>
</#list>
    <tr>
        <td colspan="3">В ночь:</td>
    </tr>
<#list nights as night>
    <tr>
        <td><b>${night.name}</b></td>
        <td><i>${night.description}</i></td>
        <td>${night.shiftType}</td>
    </tr>
<#else>
Not found shift
</#list>
</table>
</@c.page>
