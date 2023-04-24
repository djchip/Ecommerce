import { ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from './../../forms/forms.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoreCommonModule } from '@core/common.module';
import { ComingSoonComponent } from 'app/main/pages/miscellaneous/coming-soon/coming-soon.component';
import { ErrorComponent } from 'app/main/pages/miscellaneous/error/error.component';
import { MaintenanceComponent } from 'app/main/pages/miscellaneous/maintenance/maintenance.component';
import { NotAuthorizedComponent } from 'app/main/pages/miscellaneous/not-authorized/not-authorized.component';
import { ForgotPasswordSentComponent } from 'app/main/pages/miscellaneous/forgot-password-sent/forgot-password-sent';
import { ForgotPasswordConfirmComponent } from 'app/main/pages/miscellaneous/forgot-password-confirm/forgot-password-confirm';

// routing
const routes: Routes = [
  {
    path: 'miscellaneous/coming-soon',
    component: ComingSoonComponent
  },
  {
    path: 'miscellaneous/not-authorized',
    component: NotAuthorizedComponent
  },
  {
    path: 'miscellaneous/maintenance',
    component: MaintenanceComponent
  },
  {
    path: 'miscellaneous/error',
    component: ErrorComponent
  },
  {
    path: 'miscellaneous/forgot-password-sent',
    component: ForgotPasswordSentComponent
  },
  {
    path: 'miscellaneous/forgot-password-confirm',
    component: ForgotPasswordConfirmComponent
  }
];

@NgModule({
  declarations: [ComingSoonComponent, NotAuthorizedComponent, MaintenanceComponent, ErrorComponent, ForgotPasswordSentComponent,ForgotPasswordConfirmComponent],
  imports: [CommonModule, RouterModule.forChild(routes), NgbModule, FormsModule, ReactiveFormsModule, CoreCommonModule]
})
export class MiscellaneousModule {}