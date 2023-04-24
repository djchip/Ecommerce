import { AutoUploadModule } from './../auto-upload/auto-upload.module';
import { AutoImportModule } from './../auto-import/auto-import.module';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgSelectModule } from '@ng-select/ng-select';
import { CoreDirectivesModule } from '@core/directives/directives';
import { FormsModule } from '@angular/forms';
import { CardSnippetModule } from '@core/components/card-snippet/card-snippet.module';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { CoreCommonModule } from '@core/common.module';
import { UploadFormWizardComponent } from './upload-form-wizard.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

const routes: Routes = [
  {
    path: 'upload-form-wizard',
    component: UploadFormWizardComponent,
    data: { animation: 'wizard' }
  }
];

@NgModule({
  declarations: [
    UploadFormWizardComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    CoreCommonModule,
    ContentHeaderModule,
    CardSnippetModule,
    FormsModule,
    CoreDirectivesModule,
    NgSelectModule,
    TranslateModule,
    NgbModule,
    CorePipesModule,
    AutoUploadModule,
    AutoImportModule,
  ]
})
export class UploadFormWizardModule { }
