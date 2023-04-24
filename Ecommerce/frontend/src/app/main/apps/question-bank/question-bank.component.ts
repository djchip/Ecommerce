import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbCalendar, NgbDate, NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import { QuestionBankService } from './question-bank.service';

@Component({
  selector: 'app-question-bank',
  templateUrl: './question-bank.component.html',
  styleUrls: ['./question-bank.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class QuestionBankComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public addBotForm: FormGroup;
  public contentHeader: any;
  public basicDPdata1: NgbDateStruct;
  public basicDPdata2: NgbDateStruct;

  constructor(
    private modalService: NgbModal,
    private fb: FormBuilder,
    private questionBankService: QuestionBankService,
    private calendar: NgbCalendar
  ) {
    this.contentHeader = {
      headerTitle: 'Ngân hàng câu hỏi- ý định',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Trang chủ',
            isLink: true,
            link: '/'
          },

          {
            name: 'Ngân hàng câu hỏi- ý định',
            isLink: true,
            link: 'apps/question-bank'
          }
        ]
      }
    };


  }

  ngOnInit(): void {
    this.initData();
  }

  public intents = [];
  public synonyms = [];
  public users = [];
  public entitys = [];
  public searchForm: FormGroup;

  initData() {
    this.getUserList();
    this.createSearchForm();
    this.getEntitytList();
    this.getIntentList();
    this.getSynonymList();
    this.getListIntent();
  }

  createSearchForm() {
    this.searchForm = this.fb.group({
      intent: '',
      user: '',
      entity: '',
      synonym: '',
      timeApplyStart: '',
      timeApplyEnd: ''
    })
  }

  getUserList() {
    let params = {
      method: 'GET'
    }
    this.questionBankService.getUserList(params).then(
      res => {
        if (res.code == 0) {
          this.users = res?.content;
          console.log('entity', this.users)
        }
      }
    )
  }

  getEntitytList() {
    let params = {
      method: 'GET'
    }
    this.questionBankService.getEntitytList(params).then(
      res => {
        if (res.code == 0) {
          this.entitys = res?.content;
          console.log('entity', this.entitys)
        }
      }
    )
  }

  getIntentList() {
    let params = {
      method: 'GET'
    }
    this.questionBankService.getListIntentName(params).then(
      res => {
        if (res.code == 0) {
          this.intents = res?.content;
          console.log('synonyms', this.intents)
        }
      }
    )
  }


  getSynonymList() {
    let params = {
      method: 'GET'
    }
    this.questionBankService.getSynonymList(params).then(
      res => {
        if (res.code == 0) {
          this.synonyms = res?.content;
          console.log('synonyms', this.synonyms)
        }
      }
    )
  }

  formatDate(item) {
    let format = '';
    if (item != null && item != undefined && item != '') {
      let day = (item.day < 10) ? ("0" + item.day) : (item.day);
      let month = (item.month < 10) ? ("0" + item.month) : (item.month);
      let year = item.year;
      format = day + "-" + month + "-" + year;
    }
    return format;
  }

  dateMessage: any
  result: any
  getListIntent() {

    let params = {
      method: 'GET',
      fromDate: this.formatDate(this.searchForm.value.timeApplyStart),
      toDate: this.formatDate(this.searchForm.value.timeApplyEnd),
      intentName: this.searchForm.value.intent,
      createdBy: this.searchForm.value.user,
      entityName: this.searchForm.value.entity,
      synonymContent: this.searchForm.value.synonym,
      currentPage: this.currentPage,
      perPage: this.perPage
    }
    console.log('itent', this.searchForm.value.intent)
    if (this.validate()) {
      this.questionBankService.getIntentList(params).then(
        res => {
          if (res.code == 0) {
            this.rows = res?.content.content
            console.log(this.rows)
            this.totalRows = res?.content.totalElements
          }
          else {
            this.totalRows = 0;
          }
        }
      )

    }
  }

  searchIntentList() {
    this.currentPage = 0;
    this.getListIntent();
  }

  validate() {
    let isValid = true;
    let startDate = this.searchForm.value.timeApplyStart;
    let endDate = this.searchForm.value.timeApplyEnd;

    if (startDate != null && startDate != undefined && endDate != null && endDate != undefined) {
      let compStartDate = new Date(startDate.year, startDate.month, startDate.day);
      let compEndDate = new Date(endDate.year, endDate.month, endDate.day);
      if (compStartDate.getTime() > compEndDate.getTime()) {
        this.dateMessage = 'Ngày kết thúc lớn hơn ngày bắt đầu';
        isValid = false
      }
    }
    return isValid
  }
  clearDateMessage() {
    this.dateMessage = '';
  }

  currentPage = 0;
  public totalRows = 0;
  public perPage = 10;
  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.getListIntent();
  }


  openModal(modal) {
    this.modalService.open(modal, {
      centered: true,
      backdrop: 'static',
      windowClass: "modal",
      size: "xl",
    });
  }


  searchTerm = '';
  isWeekend = (date: NgbDate) => this.calendar.getWeekday(date) >= 6;
  isDisabled = (date: NgbDate, current: { month: number; year: number }) =>
    date.month !== current.month;
  maxDate = { year: 2030, month: 12, day: 31 };
  minDate = { year: 1900, month: 1, day: 1 };
}
