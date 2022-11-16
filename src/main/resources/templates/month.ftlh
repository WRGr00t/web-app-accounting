<#import "parts/common.ftlh" as c>
<@c.page>
<div class="container">
    <h2>Анализ рабочего времени за месяц по сотрудникам</h2>
        <div class="input-group date">
          <input type="text" class="form-control"><span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
        </div>
</div>
<script>
       $('#container.input-group.date').datepicker({
          format: "yyyy-dd",
          minViewMode: 1,
          language: "ru"
       });
    </script>
</@c.page>