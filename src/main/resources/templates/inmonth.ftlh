<#import "parts/common.ftlh" as c>
<@c.page>
<div class="container">
    <form>
        <div class="form-group">
            <div class="col-sm-6">
                <div class="input-group date">
                    <input class="form-control" type="date" name="start" value=${dateStart}
                            max=${endYear} min=${startYear}>
                    <input class="form-control" type="date" name="end" value=${dateEnd}
                            max=${endYear} min=${startYear}>
                    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Выбрать</button>
                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                </div>
            </div>
        </div>
    </form>

    <table class="table table-striped">
      <thead>
            <tr>
                <th scope="col">Сотрудник</th>
                <th scope="col">Общее время</th>
           </tr>
       </thead>
    <#list repos?keys as key>
        <tr scope="row">
            <td>${key}</td>
            <td><i>${repos[key]}</i></td>
        </tr>
    <#else>
    Нет данных для отображения
    </#list>
    </table>
</div>
</@c.page>