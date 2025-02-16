import DP from 'flowbite-datepicker/Datepicker';


export class Datepicker {
  constructor(id) {
    console.log('init Datepicker')
    this._dp = createDatepicker(id)
  }


  getDate() {
    return this._dp.getDate()
  }

}

function createDatepicker(elementId) {
  const $datepickerEl = document.getElementById(elementId);

  if (!$datepickerEl) {
    console.error("Invalid element ID for datepicker.");
    return;
  }

  const options = {
    defaultDatepickerId: null,
    autohide: true,
    format: 'MM dd yyyy',
    maxDate: null,
    minDate: new Date(),
    orientation: 'bottom',
    buttons: true,
    autoSelectToday: true,
    title: 'Due Date for this Todo',
    rangePicker: false,
  };

  return new DP($datepickerEl, options);
}
