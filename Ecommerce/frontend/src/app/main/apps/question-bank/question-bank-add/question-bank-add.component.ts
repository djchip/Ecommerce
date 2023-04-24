import { Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import { TextSelectEvent } from "@core/directives/text-select/text-select.directives";
import { QuestionBankService } from "../question-bank.service";
import { FormControl, FormGroup } from "@angular/forms";
import { BotSynonymPy, Entity, Question } from './question-bank-add';
import { NgbDropdown } from "@ng-bootstrap/ng-bootstrap";
import Swal from "sweetalert2";

@Component({
  selector: "app-question-bank-add",
  templateUrl: "./question-bank-add.component.html",
  styleUrls: ["./question-bank-add.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class QuestionBankAddComponent implements OnInit {
  addFG: FormGroup;
  @ViewChild(NgbDropdown) private dropdown: NgbDropdown;

  constructor(private questionBankService: QuestionBankService) { }

  ngOnInit(): void {
    this.createQuestionArray();
    this.getEntitytList();
    this.addFormGr()
  }

  closeDropdown() {
    console.log('myDrop', this.dropdown)
    this.dropdown.close();
  }

  addFormGr() {
    this.addFG = new FormGroup({
      intentName: new FormControl(),
      intentCode: new FormControl(),
    });
  }

  public questions: Question[];
  public entitySelected: any;

  createQuestionArray() {
    this.questions = [
      new Question(),
      new Question(),
      new Question(),
      new Question(),
      new Question(),
    ];
    console.log("question", this.questions);
  }

  private selectText = "";
  public renderRectangles(event: TextSelectEvent): void {
    this.selectText = event.text;
  }
  text: any
  entity: Entity;
  entitys: Entity[];
  public shareSelection(i): void {
    this.text = this.selectText;
    console.log('ques', this.questions)
  }
  messageEntity = ''
  messageText = ''
  share(i) {
    console.log('text', this.text)
    if (this.text == '') {
      Swal.fire('',
        'Bạn phải bôi đen chữ trước!',
        'error').then(
          (result) => {
            if (result.value)
              this.closeDropdown();
          }
        )
    } else {
      if (this.entitySelected == null) {
        this.messageEntity = 'Bạn chưa chọn thực thể'
      }

      if (this.text != '' && this.entitySelected != null) {
        const entity = {
          content: this.text,
          entityName: this.entitySelected,
          botSynonymPy: [new BotSynonymPy()]
        }
        console.log(i);
        this.addEnity(i, entity)
        console.log(this.questions)
        this.closeDropdown();
        this.text = '',
          this.entitySelected = null
      }
    }

  }
  subEntity(i, w) {
    const length = this.questions[i].entity.length
    for (let j = 0; j < length; j++) {
      if (w == j) {
        this.questions[i].entity.splice(j, 1)
      }
    }
  }

  changeQuestion(i, e) {
    this.questions[i].entity = []

  }

  addQuestion() {
    this.questions.push(new Question());
  }

  addEnity(i, entity) {
    this.questions[i].entity.push(entity);
  }

  deleteQuestion(id) {
    for (let i = 0; i < this.questions.length; i++) {

        this.questions[i].questionName='';
        this.questions[i].entity=[];
      }
    }
  

  public selectTag: any;
  public customTag: any[] = [];

  selectAddTagMethod = (name) => {
    this.customTag.push(name);
    return { entityName: name, tag: true };
  };

  getEntitytList() {
    let params = {
      method: "GET",
    };
    this.questionBankService.getEntitytList(params).then((res) => {
      if (res.code == 0) {
        this.customTag = res?.content;
      }
    });
  }

  changeEntity(e) {
    console.log('e', e);
    if (e != '') {
      this.messageEntity = ''
    }
  }
  symphon = "";
  listSymphon: any[] = [];
  setSymphon() {
    if (this.symphon !== "") {
      this.listSymphon.push(this.symphon);
      this.symphon = "";
    }
  }

  simililarQuestion: any;
  getQuestionSimlilar(i) {
    let params = {
      method: "GET",
      num: 3,
      query: this.questions[i].questionName,
    };
    this.questionBankService.getQuestionSimlilar(params).then((res) => {
      this.simililarQuestion = res;
      this.wordLine = new Array(this.simililarQuestion.length);
    });
  }

  wordLine = [];
  question1 = [];
  word = [];
  getWord(i, j, z) {
    if (z == this.wordLine[j]) {
      this.wordLine[j] = -5;
    } else {
      this.wordLine[j] = z;
    }
  }



  saveQuestion() {
    for (let x = 0; x < this.simililarQuestion.length; x++) {
      this.wordLine.forEach((e, i) => {
        if (x == i) {
          if (e != -5) {
            this.question1.push(this.simililarQuestion[x][e]);
          }
        }
      });
    }
    if (this.question1 == [] || this.question1 == undefined) {
      return;
    } else {
      console.log("abc", this.questions);
      const indexQuestion = this.questions.findIndex(
        (e) => e.questionName == ""
      );
      if (indexQuestion == -1) {
        this.addQuestion();
        const question = this.question1.join(" ");
        this.questions[this.questions.length - 1].questionName = question;
        this.wordLine = new Array(this.simililarQuestion.length);
        this.question1 = [];
      }
      const question = this.question1.join(" ");
      this.questions[indexQuestion].questionName = question;
      this.wordLine = new Array(this.simililarQuestion.length);
      this.question1 = [];
    }
  }

  listbotSynonymPy=[];

  deleteSynonym(u) {
    this.listSymphon.splice(u, 1);
    
  }

  saveSynonym(i, w) {
    this.listSymphon.forEach(e=>{
      this.listbotSynonymPy.push({
        synonymContent:e
      })
    })
    this.questions[i].entity[w].botSynonymPy = this.listbotSynonymPy;
    console.log(this.questions);
    this.listSymphon=[];
    this.listbotSynonymPy=[]
  }

  saveIntent(){
    this.questionBankService.addIntent(
      {
        method:'POST',
        content: {
          intentCode:this.addFG.value.intentCode,
          intentName:this.addFG.value.intentName,
          questions:this.questions}
      }
    ). then(
      res=>{
        if (res.code == 0) {
          Swal.fire({
            icon: "success",
            title: "Thêm mới thành công.",
          })
        }
        else {
          Swal.fire({
            icon: "error",
            title: "Thêm mới không thành công",
          })
        }
      }).catch((error) => {
        Swal.fire({
          icon: "error",
          title: "Khống thể kết nối tới hệ thống.",
        })
      }
    )
  }
}
