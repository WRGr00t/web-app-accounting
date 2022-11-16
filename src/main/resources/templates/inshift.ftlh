<#import "parts/common.ftlh" as c>
<@c.page>
<div class="container">
    <form>
        <div class="form-group">
            <div class="col-sm-6">
                <div class='input-group date'>
                    <input class="form-control" type="date" name="calendar" value=${date}
                           max=${endYear} min=${startYear}>
                    <button class="btn btn-primary" type="submit">Выбрать</button>
                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                </div>
            </div>
        </div>
    </form>
</div>
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
