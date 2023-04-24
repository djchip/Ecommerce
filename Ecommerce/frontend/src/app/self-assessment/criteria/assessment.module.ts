import { TranslateModule } from '@ngx-translate/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from './../../layout/components/content-header/content-header.module';
import { RouterModule, Routes } from '@angular/router';
import { DetailAssessmentComponent } from './detail-assessment/detail-assessment.component';
import { EditAssessmentComponent } from './edit-assessment/edit-assessment.component';
import { AddAssessmentComponent } from './add-assessment/add-assessment.component';
import { ListAssessmentComponent } from './list-assessment/list-assessment.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

const routes: Routes = [
  {
    path: 'list',
    component: ListAssessmentComponent,
    data: {animation: 'feather'}
  }
]

@NgModule({
  declarations: [
    ListAssessmentComponent,
    AddAssessmentComponent,
    EditAssessmentComponent,
    DetailAssessmentComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    CorePipesModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    TranslateModule
  ],
  exports:[
    DetailAssessmentComponent,
  ]
})
export class AssessmentModule { }
