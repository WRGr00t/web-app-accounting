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
                <th scope="col">Количество смен</th>
                <th scope="col">Из них без обеда</th>
                <th scope="col">Итог в часах</th>
           </tr>
       </thead>
    <#list repos as repo>
        <tr scope="row">
            <td>${repo.name}</td>
            <td>${repo.countHours}</td>
            <td>${repo.countShifts}</td>
            <td>${repo.countShiftsWithoutDinner}</td>
            <td>${repo.countHours - repo.countShifts + repo.countShiftsWithoutDinner}</td>
        </tr>
    <#else>
    Нет данных для отображения
    </#list>
    </table>
</div>
</@c.page>