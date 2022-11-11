<#import "parts/common.ftlh" as c>

<@c.page>
<form class="row g-3">
    <div class="mb-3">
      <label for="formFile" class="form-label">Выберите CSV-файл для загрузки</label>
      <input class="form-control" type="file" id="formFile" name="file" accept="application/csvm+json">
        <div class="col-auto">
              <button type="submit" class="btn btn-primary mb-3">Загрузить</button>
              <input type="hidden" name="_csrf" value="${_csrf.token}"/>
            </div>
    </div>
    </div>

</@c.page>