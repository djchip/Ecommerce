import { AdministratorsModule } from './../administrators.module';
import { DetailUserComponent } from './../user-management/detail-user/detail-user.component';
import { TranslateModule } from '@ngx-translate/core';
import { ContentHeaderModule } from './../../layout/components/content-header/content-header.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreDirectivesModule } from './../../../@core/directives/directives';
import { CorePipesModule } from './../../../@core/pipes/pipes.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { Routes, RouterModule } from '@angular/router';
import { LogUndoComponent } from './log-undo.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { ExhibitionModule } from 'app/exhibition/exhibition.module';
import { AcademyDatabaseModule } from 'app/academy-database/academy-database.module';
import { SelfAssessmentModule } from 'app/self-assessment/self-assessment.module';

const routes: Routes = [
  {
    path: 'log-undo',
    component: LogUndoComponent,
  },

];

@NgModule({
  declarations: [
    LogUndoComponent
  ],
  imports: [
    NgSelectModule,
    RouterModule.forChild(routes),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    ContentHeaderModule,
    TranslateModule,
    AdministratorsModule,
    ExhibitionModule,
    AcademyDatabaseModule,
    SelfAssessmentModule,
  ],
  exports: [
    // DetailUserComponent
  ]
})
export class LogUndoModule { }
