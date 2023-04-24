import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CoreDirectivesModule } from '@core/directives/directives';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { QuillModule } from 'ngx-quill';
import { NgSelectModule } from '@ng-select/ng-select';

import { CoreCommonModule } from '@core/common.module';
import { CardSnippetModule } from '@core/components/card-snippet/card-snippet.module';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { QuestionBankComponent } from './question-bank.component';
import { QuestionBankAddComponent } from './question-bank-add/question-bank-add.component';
const routes: Routes = [
  {
    path: 'list',
    component: QuestionBankComponent,
  },
];

@NgModule({
  declarations: [QuestionBankComponent, QuestionBankAddComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgxDatatableModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    CoreDirectivesModule,
    CorePipesModule,
    CoreCommonModule,
    CardSnippetModule,
    ContentHeaderModule,
    NgSelectModule,
    QuillModule.forRoot(),
  ]
})
export class QuestionBankModule { }
