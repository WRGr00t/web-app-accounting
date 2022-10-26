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
<div>${date} на смене:</div><br>
<#list repos as repo>
<div>
    <b>${repo.name}</b> <i>${repo.description}</i> ${repo.shiftType}<br>
</div>
</#list>
<div><br>
    В ночь: <br><br>
</div>
<#list nights as night>
<div>
    <b>${night.name}</b> <i>${night.description}</i> ${night.shiftType}<br>
</div>
<#else>
Not found shift
</#list>
</@c.page>
