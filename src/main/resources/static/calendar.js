const date = new Date();
let year = date.getFullYear();
let personName;

const renderCalendar = () => {
  date.setDate(1);

  const monthDays = document.querySelector(".days");

  const months = [
    'Январь',
	'Февраль',
	'Март',
	'Апрель',
	'Май',
	'Июнь',
	'Июль',
	'Август',
	'Сентябрь',
	'Октябрь',
	'Ноябрь',
	'Декабрь'
  ];

  let shiftObj = [];

  document.querySelector(".date h1").innerHTML = months[date.getMonth()] + " " + year;

	var options = {
	  year: 'numeric',
	  month: 'long',
	  day: 'numeric',
	  weekday: 'long'
	};

  document.querySelector(".date p").innerHTML = new Date().toLocaleString("ru", options);

	const monthBox = document.createElement('div')
    monthBox.className = 'month'
	const weekdayBox = document.createElement('div');
	document.createElement('div');


  let days = "";
  while (monthDays.firstChild) {
      monthDays.removeChild(monthDays.firstChild);
  }
  let dayIndex = 0;

  const firstDayIndex = getFirstDayIndex(date);
  const prevLastDay = getPrevLastDay(date);

  let datesHTML = [];


  //все даты с начала месяца и раньше первой даты месяца маркируем как "Предыдущие даты"
      for (let x = firstDayIndex - 1; x > 0; x--) {
          let dateHTML;
          dateHTML = `<div class="prev-date">${prevLastDay - x + 1}</div>`;
          //console.log(prevLastDay - x + 1 + "/" + date.getMonth() + "/" + date.getFullYear());
          dayIndex++;
          datesHTML.push(dateHTML);
      }
  days = datesHTML.join(' ');

  //заполнение дат в текущем месяце
  datesHTML = [];
      let cls;
      let status;
      let requestURL = "/api/bynameandmonth?name=" + name + "&year=" + year;
      //console.log(requestURL);
      let job = fetch(requestURL).then(
            successResponse => {
              if (successResponse.status != 200) {
                //onsole.log("not success");
                return null;
              } else {
                //console.log("success");
                return successResponse.json();
              }
            },
            failResponse => {
              return null;
            }
      );
      let results = job.then(response => response.json());
      //console.log(results);
      for (let i = 1; i <= getLastDay(date); i++) {
          let month = date.getMonth() + 1;
          let day = normalize(i);
          month = normalize(month);
          let dateText = year + '-' + month + '-' + day;
          let status;
          if (job != null) {
                for (let key in job) {
                    let d = job[key].date;
                    if (d === dateText){
                        status = job[key].status;
                    }
                    //console.log(dateText);
                    //console.log(status);
                }
          }

          let dateHTML;
          let currentDay = new Date(date.getFullYear(), date.getMonth(), i);
          cls = (currentDay.getDay() % 7 === 0 || currentDay.getDay() % 7 === 6) ? 'accent' : 'regular';
          dayIndex++;
        	//если дата совпадает с текущей маркируем классом "Сегодня"
          if (i === new Date().getDate() &&
            date.getMonth() === new Date().getMonth() &&
            date.getFullYear() === new Date().getFullYear()) {
              cls += " today";
            }
            dateHTML = `<div class="${cls}">${i}</div>`;
          datesHTML.push(dateHTML);
          cls = '';
      }
  days += datesHTML.join(' ');

  datesHTML = [];
      for (let j = 1; j <= getNextDay(date); j++) {
          let dateHTML;
          dateHTML = `<div class="next-date">${j}</div>`;
          datesHTML.push(dateHTML);
      }
  days += datesHTML.join(' ');

  monthDays.innerHTML = days;
};

document.querySelector(".prev").addEventListener("click", () => {
  date.setMonth(date.getMonth() - 1);
  getShiftObjs(name);
  renderCalendar();
});

document.querySelector(".next").addEventListener("click", () => {
  date.setMonth(date.getMonth() + 1);
  getShiftObjs(name);
  renderCalendar();
});
let person = document.getElementById('person');
const weekDayNames = ['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ', 'ВС'];
const daysNames = [];
	for (let i = 0; i < 7; i++) {
		let dayNameTag;
		if (i < 5) {
			dayNameTag = `<div class="regular">${weekDayNames[i]}</div>`;
		} else {
			dayNameTag = `<div class="accent">${weekDayNames[i]}</div>`;
		}
		daysNames.push(dayNameTag);
	}
document.getElementById('weekdays').innerHTML = daysNames.join('');

function buildNextDates(date) {
    	//все даты с конца месяца в количестве дней до конца недели маркируем как "Следующие даты"
    const datesHTML = [];
    for (let j = 1; j <= getNextDay(date); j++) {
        let dateHTML;
        dateHTML = `<div class="next-date">${j}</div>`;
        datesHTML.push(dateHTML);
    }
    return datesHTML.join(' ');
}

function getFirstDayIndex(date) {
    //день недели первого дня месяца (0 - ВС, .. 6 - СБ)
      let firstDayIndex = date.getDay();
      if (firstDayIndex === 0) {
      	firstDayIndex = 7;
      }
    return firstDayIndex;
}

function getPrevLastDay(date) {
    // Последний день предыдущего месяца
    return new Date(
        date.getFullYear(),
        date.getMonth(),
        0).getDate();
}

function getLastDay(date) {
    // Последний день месяца
      return new Date(
        date.getFullYear(),
        date.getMonth() + 1,
        0
      ).getDate();
}

function getLastDayIndex(date) {
    //день недели последнего дня месяца
    let lastDayIndex = new Date(
       date.getFullYear(),
       date.getMonth() + 1,
       0).getDay();

    if (lastDayIndex === 0) {
        	lastDayIndex = 7;
    }
    return lastDayIndex;
}

function getNextDay(date) {
    //количество дней после конца месяца
      return (7 - getLastDayIndex(date));
}

function getPerson(name) {
    getShiftObjs(name).then(function(value)
        {
            shiftObj = value;
            personName = name;
            renderCalendar();
        })
    }

function getShiftObjs(name) {
    let monthNumber = date.getMonth() + 1;
    let requestURL = "/api/bynameandmonth?name=" + name + "&year=" + year;
    return fetch(requestURL)
        .then((result) => result.json())
}

function normalize(string) {
    if(string < 10) {
      string = '0' + string
    }
    return string;
}

renderCalendar();
